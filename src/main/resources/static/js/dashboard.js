function confirmDelete(btn) {
    var choreId = btn.dataset.choreId;
    Swal.fire({
        title: 'Delete chore?',
        text: 'Delete chore?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it'
    }).then(function (result) {
        if (result.isConfirmed) {
            htmx.ajax('DELETE', '/chores/' + choreId, {
                target: '#chore-list',
                swap: 'outerHTML'
            });
        }
    });
}

function confirmComplete(btn) {
    var choreId = btn.dataset.choreId;
    Swal.fire({
        title: 'Complete chore?',
        text: 'Mark chore as done?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#16a34a',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Yes, complete it'
    }).then(function (result) {
        if (result.isConfirmed) {
            htmx.ajax('POST', '/chores/' + choreId + '/complete', {
                target: '#chore-list',
                swap: 'outerHTML'
            });
        }
    });
}

function toggleDaysField(el) {
    var form = el ? el.closest('form') : document.querySelector('#add-chore-modal form');
    var field = form.querySelector('[name=days]');
    var daysGroup = form.querySelector('.recurring-days');
    var checked = form.querySelector('[name=scheduleType]:checked');
    var scheduleType = checked ? checked.value : el.value;
    var visible = scheduleType === 'every_n_days';
    if (daysGroup) daysGroup.style.display = visible ? '' : 'none';
    field.disabled = !visible;
    field.required = visible;
}

function openModal() {
    var form = document.querySelector('#add-chore-modal form');
    form.reset();
    form.querySelector('[name=dueDate]').value = new Date().toISOString().split('T')[0];
    form.querySelector('[name=scheduleType][value=one_time]').checked = true;
    toggleDaysField();
    document.getElementById('add-chore-modal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('add-chore-modal').classList.add('hidden');
}

function closeEditModal() {
    document.getElementById('edit-chore-modal').classList.add('hidden');
}

function openDetailModal(el) {
    var id = el.dataset.choreId;
    htmx.ajax('GET', '/chores/' + id + '/detail', {
        target: '#detail-modal-content',
        swap: 'innerHTML'
    });
    document.getElementById('detail-chore-modal').classList.remove('hidden');
}

function closeDetailModal() {
    document.getElementById('detail-chore-modal').classList.add('hidden');
}

var toastTimer = null;

function showToast(message) {
    var toast = document.getElementById('toast');
    var msg = document.getElementById('toast-message');
    if (!toast || !msg) return;
    msg.textContent = message;
    toast.classList.remove('hidden');
    setTimeout(function () { toast.classList.add('opacity-100'); }, 10);
    clearTimeout(toastTimer);
    toastTimer = setTimeout(dismissToast, 5000);
}

function dismissToast() {
    var toast = document.getElementById('toast');
    if (!toast) return;
    toast.classList.remove('opacity-100');
    setTimeout(function () { toast.classList.add('hidden'); }, 300);
    clearTimeout(toastTimer);
}

document.addEventListener('click', function (e) {
    var addModal = document.getElementById('add-chore-modal');
    if (e.target === addModal) closeModal();
});

document.addEventListener('showToast', function (e) {
    showToast(e.detail.value);
});

function setupSwipeReveal() {
    var state = { startX: 0, deltaX: 0, moved: false, container: null };

    document.addEventListener('touchstart', function (e) {
        var container = e.target.closest('.swipe-reveal-container');
        if (!container || !e.target.closest('.swipe-reveal-content')) {
            state.container = null;
            return;
        }
        state.startX = e.touches[0].clientX;
        state.deltaX = 0;
        state.moved = false;
        state.container = container;
    }, { passive: true });

    document.addEventListener('touchmove', function (e) {
        if (!state.container) return;
        var delta = state.startX - e.touches[0].clientX;
        if (delta < 3) {
            state.container.querySelector('.swipe-reveal-content').style.transform = '';
            return;
        }
        e.preventDefault();
        state.moved = true;
        state.deltaX = delta;
        var content = state.container.querySelector('.swipe-reveal-content');
        var reveal = state.container.querySelector('.swipe-reveal-action');
        var maxSwipe = reveal.offsetWidth;
        content.style.transition = 'none';
        content.style.transform = 'translateX(-' + Math.min(delta, maxSwipe) + 'px)';
    }, { passive: false });

    document.addEventListener('touchend', function () {
        if (!state.container) return;
        var container = state.container;
        var content = container.querySelector('.swipe-reveal-content');
        var reveal = container.querySelector('.swipe-reveal-action');
        content.style.transition = 'transform 0.2s ease-out';

        if (!state.moved || state.deltaX < 30) {
            content.style.transform = '';
            container.classList.remove('is-revealed');
            state.container = null;
            return;
        }

        var maxSwipe = reveal.offsetWidth;
        if (state.deltaX > maxSwipe / 2) {
            content.style.transform = 'translateX(-' + maxSwipe + 'px)';
            container.classList.add('is-revealed');
        } else {
            content.style.transform = '';
            container.classList.remove('is-revealed');
        }
        state.container = null;
    });

    document.addEventListener('click', function (e) {
        var container = e.target.closest('.swipe-reveal-container');
        document.querySelectorAll('.swipe-reveal-container.is-revealed').forEach(function (c) {
            if (c !== container) {
                c.querySelector('.swipe-reveal-content').style.transform = '';
                c.classList.remove('is-revealed');
            }
        });
    });
}

setupSwipeReveal();

var origOpenDetail = window.openDetailModal;
window.openDetailModal = function (el) {
    var container = el.closest('.swipe-reveal-container');
    if (container && container.classList.contains('is-revealed')) return;
    origOpenDetail(el);
};
