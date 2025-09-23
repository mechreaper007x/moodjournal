document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            event.preventDefault();
            
            // --- MOCK REGISTRATION ---
            // In a real application, you would send the username and password
            // to a backend endpoint to create a new user.
            // For this example, we'll just simulate a successful registration.
            
            alert('Registration successful! You can now log in.');
            window.location.href = '/login.html';
        });
    }
});