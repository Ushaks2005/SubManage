
const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const role = document.getElementById("role").value;
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("errorMessage");
    const loginButton = loginForm.querySelector(".login-button");

    errorMessage.textContent = "";

    // 1. Validate role
    if (role === "") {
        errorMessage.textContent = "Please select your login role.";
        return;
    }

    // 2. Validate email
    if (email === "") {
        errorMessage.textContent = "Please enter your email address.";
        return;
    }

    // 3. Validate email format
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
        errorMessage.textContent = "Please enter a valid email address.";
        return;
    }

    // 4. Validate password
    if (password.trim() === "") {
        errorMessage.textContent = "Please enter your password.";
        return;
    }

    // 5. Send login request
    loginButton.disabled = true;
    loginButton.textContent = "Signing in...";

    try {

        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                role: role,
                email: email,
                password: password
            })
        });

        const result = await response.json();

        if (!response.ok) {
            errorMessage.textContent =
                result.message || "Invalid email or password.";
            return;
        }

        // 6. Redirect based on authenticated role
        if (result.role === "ADMIN") {
            window.location.href = "/admin/dashboard";
        } else if (result.role === "CUSTOMER") {
            window.location.href = "/customer/dashboard";
        } else {
            errorMessage.textContent = "Invalid user role.";
        }

    } catch (error) {

        console.error("Login error:", error);

        errorMessage.textContent =
            "Unable to connect to the server. Please try again.";

    } finally {

        loginButton.disabled = false;
        loginButton.textContent = "Sign In";

    }

});
