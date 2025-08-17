const baseUrl = "http://localhost:8080/";
let contactModal;
document.addEventListener('DOMContentLoaded', () => {
    const viewContactModal = document.getElementById('view_contact_modal');

    // options with default values
    const options = {
        placement: 'bottom-right',
        backdrop: 'dynamic',
        backdropClasses:
            'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
        closable: true,
        onHide: () => {
            console.log('modal is hidden');
        },
        onShow: () => {
            console.log('modal is shown');
        },
        onToggle: () => {
            console.log('modal has been toggled');
        },
    };

    // instance options object
    const instanceOptions = {
        id: 'view_contact_modal',
        override: true
    };

    /* global Modal */
    if (viewContactModal) {
        contactModal = new Modal(viewContactModal, options, instanceOptions);
    }
});

function openContactModal() {
    if (contactModal) contactModal.show();
}

function closeContactModal() {
    if (contactModal) contactModal.hide();
}

function loadContactData(id) {
    fetch(`${baseUrl}api/contacts/${id}`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            document.querySelector("#contact_name").innerHTML = data.name;
            document.querySelector("#contact_email").innerHTML = data.email;
            document.querySelector("#contact_image").src = data.contactImage;
            document.querySelector("#contact_phone").textContent = data.phoneNumber;
            document.querySelector("#contact_address").textContent = data.address;
            document.querySelector("#contact_description").textContent = data.description;

            // Favorite → display stars if true
            document.querySelector("#contact_favorite").textContent = data.favorite ? "★★★★★" : "☆☆☆☆☆";

            // Website
            if (data.websiteLink) {
                const websiteEl = document.querySelector("#contact_website");
                websiteEl.href = data.websiteLink;
                websiteEl.textContent = data.websiteLink;
            }

            // LinkedIn
            if (data.linkedInLink) {
                const linkedinEl = document.querySelector("#contact_linkedin");
                linkedinEl.href = data.linkedInLink;
                linkedinEl.textContent = data.linkedInLink;
            }
            openContactModal();
        })
        .catch(error => {
            console.error("Error fetching contact:", error);
        });
}

// function deleteContact(id) {
//     Swal.fire({
//         title: "Are you sure?",
//         text: "You want to delete this contact!",
//         icon: "warning",
//         showCancelButton: true,
//         confirmButtonColor: "#3085d6",
//         cancelButtonColor: "#d33",
//         confirmButtonText: "Yes, delete it!"
//     }).then((result) => {
//         if (result.isConfirmed) {
//             Swal.fire({
//                 title: "Deleted!",
//                 text: "Your Contact has been deleted.",
//                 icon: "success"
//             });
//             const url = `${baseUrl}user/contacts/delete/` + id;
//             window.location.replace(url);
//         }
//     });
// }

function openDeleteModal(id) {
    const deleteBtn = document.getElementById("deleteConfirmBtn");
    deleteBtn.onclick = function () {
        const url = `${baseUrl}user/contacts/delete/` + id;
        window.location.replace(url);
    };

    const deleteModal = document.getElementById("popup-modal");
    deleteModal.classList.remove("hidden");
    deleteModal.classList.add("flex");
}

document.querySelectorAll("[data-modal-hide='popup-modal']").forEach(el => {
    el.addEventListener("click", () => {
        const deleteModal = document.getElementById("popup-modal");
        deleteModal.classList.add("hidden");
        deleteModal.classList.remove("flex");
    });
});


window.openContactModal = openContactModal;
window.closeContactModal = closeContactModal;
window.loadContactData = loadContactData;
window.openDeleteModal = openDeleteModal;
//window.deleteContact = deleteContact;