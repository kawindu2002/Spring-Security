$(document).ready(function () {
        $("#createAccount").on('click', function (e) {
                e.preventDefault();
                const name = $('#signupName').val();
                const username = $('#signupUsername').val();
                const password = $('#signupPassword').val();
                const confirmPassword = $('#signupConfirmPassword').val();
                const role = $('#userRole').val();
                
                if (password !== confirmPassword) {
                        alert("Passwords do not match!");
                        return;
                }
                
                const data = {
                        userName: username,
                        password: password,
                        role: role
                };
                
                $.ajax({
                        url: 'http://localhost:8080/auth/register',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function (response) {
                                alert("Account created successfully!");
                                window.location.href = "../pages/index.html";
                        },
                        error: function (xhr) {
                                alert("Sign up failed: " + xhr.responseText);
                        }
                });
        });
});

