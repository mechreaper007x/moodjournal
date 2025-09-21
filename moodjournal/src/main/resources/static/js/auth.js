document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const showRegister = document.getElementById('show-register');
    const showLogin = document.getElementById('show-login');
    const loginFormContainer = document.getElementById('login-form-container');
    const registerFormContainer = document.getElementById('register-form-container');

    showRegister.addEventListener('click', (e) => {
        e.preventDefault();
        loginFormContainer.style.display = 'none';
        registerFormContainer.style.display = 'block';
    });

    showLogin.addEventListener('click', (e) => {
        e.preventDefault();
        loginFormContainer.style.display = 'block';
        registerFormContainer.style.display = 'none';
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            if (response.ok) {
                const user = await response.json();
                localStorage.setItem('userId', user.id);
                window.location.href = '/index.html';
            } else {
                alert('Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('An error occurred during login.');
        }
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('register-name').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });
            if (response.ok) {
                alert('Registration successful! Please login.');
                loginFormContainer.style.display = 'block';
                registerFormContainer.style.display = 'none';
            } else {
                alert('Registration failed');
            }
        } catch (error) {
            console.error('Registration error:', error);
            alert('An error occurred during registration.');
        }
    });
});