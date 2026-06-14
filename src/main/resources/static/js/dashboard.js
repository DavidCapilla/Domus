function confirmDelete(btn) {
    var choreName = btn.dataset.choreName;
    Swal.fire({
        title: 'Delete chore?',
        text: 'Delete "' + choreName + '"?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it'
    }).then(function (result) {
        if (result.isConfirmed) {
            htmx.ajax('DELETE', '/chores/' + encodeURIComponent(choreName), {
                target: '#chore-list',
                swap: 'outerHTML'
            });
        }
    });
}

function confirmComplete(btn) {
    var choreName = btn.dataset.choreName;
    Swal.fire({
        title: 'Complete chore?',
        text: 'Mark "' + choreName + '" as done?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#16a34a',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Yes, complete it'
    }).then(function (result) {
        if (result.isConfirmed) {
            htmx.ajax('POST', '/chores/' + encodeURIComponent(choreName) + '/complete', {
                target: '#chore-list',
                swap: 'outerHTML'
            });
        }
    });
}

function toggleDaysField(el) {
    var form = el ? el.closest('form') : document.querySelector('#add-chore-modal form');
    var field = form.querySelector('[name=days]');
    var checked = form.querySelector('[name=scheduleType]:checked');
    var scheduleType = checked ? checked.value : el.value;
    var visible = scheduleType === 'every_n_days';
    field.style.display = visible ? '' : 'none';
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
    var name = el.dataset.choreName;
    htmx.ajax('GET', '/chores/' + encodeURIComponent(name) + '/detail', {
        target: '#detail-modal-content',
        swap: 'innerHTML'
    });
    document.getElementById('detail-chore-modal').classList.remove('hidden');
}

function closeDetailModal() {
    document.getElementById('detail-chore-modal').classList.add('hidden');
}

document.addEventListener('click', function (e) {
    var addModal = document.getElementById('add-chore-modal');
    if (e.target === addModal) closeModal();
});
