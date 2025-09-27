/**
 * @jest-environment jsdom
 */

// Import the JavaScript file to be tested
// The login script is loaded within beforeEach to ensure a fresh state for each test.

describe('Registration Form Error Handling', () => {
    let loginFormContainer;
    let registerFormContainer;
    let showRegisterBtn;
    let showLoginBtn;
    let loginForm;
    let registerForm;
    let notificationEl;
    let originalGetElementById; // To store the original document.getElementById

    beforeEach(() => {
        // Clear mocks and reset modules to ensure a clean slate for each test
        jest.clearAllMocks();
        jest.resetModules();

        // Store the original document.getElementById
        originalGetElementById = document.getElementById;

        // Set up a basic DOM structure similar to login.html
        document.body.innerHTML = `
            <div class="auth-container">
                <!-- Login Form -->
                <div id="login-form-container">
                    <div class="auth-header">
                        <h1>Welcome Back</h1>
                        <p>Login to continue your journey</p>
                    </div>
                    <form id="login-form">
                        <div class="form-group">
                            <label for="login-email">Email</label>
                            <input type="email" id="login-email" required>
                        </div>
                        <div class="form-group">
                            <label for="login-password">Password</label>
                            <input type="password" id="login-password" required>
                        </div>
                        <button type="submit" class="btn">Login</button>
                    </form>
                    <p class="toggle-link">Don't have an account? <a id="show-register">Register here</a></p>
                </div>

                <!-- Registration Form -->
                <div id="register-form-container" style="display: none;">
                    <div class="auth-header">
                        <h1>Create Account</h1>
                        <p>Start your mental wellness journey today</p>
                    </div>
                    <form id="register-form">
                        <div class="form-group">
                            <label for="register-username">Username</label>
                            <input type="text" id="register-username" value="existinguser" required>
                        </div>
                        <div class="form-group">
                            <label for="register-email">Email</label>
                            <input type="email" id="register-email" value="existing@example.com" required>
                        </div>
                        <div class="form-group">
                            <label for="register-password">Password</label>
                            <input type="password" id="register-password" value="password123" required>
                        </div>
                        <button type="submit" class="btn">Register</button>
                    </form>
                    <p class="toggle-link">Already have an account? <a id="show-login">Login here</a></p>
                </div>
            </div>
            <div class="notification" id="notification"></div>
        `;

        // Re-initialize elements after DOM update
        loginFormContainer = document.getElementById('login-form-container');
        registerFormContainer = document.getElementById('register-form-container');
        showRegisterBtn = document.getElementById('show-register');
        showLoginBtn = document.getElementById('show-login');
        loginForm = document.getElementById('login-form');
        registerForm = document.getElementById('register-form');

        // Mock notificationEl
        notificationEl = {
            textContent: '',
            classList: {
                add: jest.fn(),
                remove: jest.fn(),
            },
            style: {
                background: '',
            },
        };
        // Replace the actual document.getElementById('notification') with our mock
        jest.spyOn(document, 'getElementById').mockImplementation((id) => {
            if (id === 'notification') {
                return notificationEl;
            }
            // Return actual elements for other IDs
            return originalGetElementById.call(document, id);
        });

        // Spy on localStorage.setItem to track calls
        jest.spyOn(window.localStorage, 'setItem');

        // Load the login script and trigger DOMContentLoaded to run it
        require('./login.js');
        document.dispatchEvent(new Event('DOMContentLoaded'));
    });

    afterEach(() => {
        // Restore the original document.getElementById
        jest.restoreAllMocks();
    });

    test('should display "Email or username already taken!" on 409 conflict during registration', async () => {
        // Simulate clicking the register button to show the registration form
        showRegisterBtn.click();

        // Simulate form submission
        registerForm.dispatchEvent(new Event('submit', { cancelable: true }));

        // Wait for the fetch promise to resolve
        await new Promise(process.nextTick); // Wait for microtasks to complete

        // Assert that the notification element's textContent was set correctly
        expect(notificationEl.textContent).toBe('Email or username already taken!');
        expect(notificationEl.classList.add).toHaveBeenCalledWith('show');
        expect(notificationEl.style.background).toBe('linear-gradient(45deg, #f5576c, #f093fb)');
    });

    test('should set localStorage on successful registration', async () => {
        

        // Simulate clicking the register button to show the registration form
        showRegisterBtn.click();

        // Change input values for a successful registration
        document.getElementById('register-username').value = 'newuser';
        document.getElementById('register-email').value = 'new@example.com';
        document.getElementById('register-password').value = 'newpassword';

        // Simulate form submission
        registerForm.dispatchEvent(new Event('submit', { cancelable: true }));

        // Wait for the fetch promise to resolve
        await new Promise(process.nextTick); // Wait for microtasks to complete

        // This console.log has been removed as it is not necessary for test validation.

        // Assert that localStorage was set
        expect(localStorage.setItem).toHaveBeenCalledWith('token', 'mock-token');
        expect(localStorage.setItem).toHaveBeenCalledWith('userId', '123');
    });
});