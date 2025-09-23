document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            
            // --- MOCK LOGIN ---
            // In a real application, you would send the username and password
            // to a backend endpoint for validation.
            // For this example, we'll just simulate a successful login
            // and store a hardcoded user ID.
            
            const username = document.getElementById('username').value;
            
            // Let's pretend user with ID 1 logged in.
            const mockUserId = 1; 
            
            localStorage.setItem('userId', mockUserId);
            localStorage.setItem('username', username);
            
            window.location.href = '/index.html';
        });
    }
});