
const registerForm = document.getElementById("registerForm");

registerForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword =
        document.getElementById("confirmPassword").value;

    const errorMessage = document.getElementById("errorMessage");
    const successMessage = document.getElementById("successMessage");
    const registerButton = document.getElementById("registerButton");

    errorMessage.textContent = "";
    successMessage.textContent = "";

    if (password !== confirmPassword) {
        errorMessage.textContent = "Passwords do not match.";
        return;
    }

    if (password.length < 8) {
        errorMessage.textContent =
            "Password must contain at least 8 characters.";
        return;
    }

    const customerData = {
        fullName: fullName,
        email: email,
        phone: phone,
        password: password
    };

    registerButton.disabled = true;
    registerButton.textContent = "Creating Account...";

    try {

        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(customerData)
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Registration failed.");
        }

        successMessage.textContent =
            "Account created successfully! Redirecting to login...";

        registerForm.reset();

        setTimeout(() => {
            window.location.href = "/login.html";
        }, 1800);

    } catch (error) {

        errorMessage.textContent = error.message;

    } finally {

        registerButton.disabled = false;
        registerButton.textContent = "Create Account";
    }
});
