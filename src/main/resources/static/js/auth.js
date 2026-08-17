const registerForm = document.getElementById("registerForm");

if (registerForm) {
    registerForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const username =
            document.getElementById("registerUsername").value;

        const email =
            document.getElementById("registerEmail").value;

        const password =
            document.getElementById("registerPassword").value;

        const confirmPassword =
            document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            alert("Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    email: email,
                    password: password
                })
            });

            const data = await response.json();

            if (response.ok) {
                alert(data.message);

                registerForm.reset();

                document.querySelector(".login").click();
            } else {
                alert(
                    data.error ||
                    data.message ||
                    "Đăng ký thất bại!"
                );
            }

        } catch (error) {
            console.error(error);
            alert("Không thể kết nối đến server!");
        }
    });
}


const loginForm = document.getElementById("loginForm");

if (loginForm) {
    loginForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const loginErrorBox = document.getElementById("loginErrorBox");
        loginErrorBox.textContent = "";
        loginErrorBox.classList.remove("show");

        const username =
            document.getElementById("loginUsername").value;

        const password =
            document.getElementById("loginPassword").value;

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            const data = await response.json();

            if (response.ok) {

                localStorage.setItem(
                    "jwt_token",
                    data.token
                );

                localStorage.setItem(
                    "user_role",
                    data.role
                );

                sessionStorage.setItem("login_success", "true");

                if (data.role === "ADMIN") {
                    window.location.href = "/admin";
                    return;
                }

                const studentResponse = await fetch(
                    "/api/students/me",
                    {
                        method: "GET",
                        headers: {
                            "Authorization":
                                `Bearer ${data.token}`
                        }
                    }
                );

                if (studentResponse.ok) {

                    window.location.href = "/home";

                } else if (studentResponse.status === 404) {

                    window.location.href =
                        "/enter-information";

                } else if (studentResponse.status === 401) {

                    localStorage.removeItem("jwt_token");

                    alert(
                        "Phiên đăng nhập không hợp lệ!"
                    );

                    window.location.href = "/login";

                } else {

                    alert(
                        "Không thể kiểm tra thông tin học sinh!"
                    );
                }

            } else {
                loginErrorBox.textContent = data.error || data.message || "Đăng nhập thất bại!";
                loginErrorBox.classList.add("show");
            }

        } catch (error) {

            console.error(error);

            loginErrorBox.textContent = "Không thể kết nối đến server!";
            loginErrorBox.classList.add("show");
        }
    });
}
