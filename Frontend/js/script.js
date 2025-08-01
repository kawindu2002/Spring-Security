$(document).ready(function() {
        // Password toggle functionality
        $('.password-toggle').click(function() {
                const icon = $(this).find('i');
                const input = $(this).siblings('input');
                
                if (input.attr('type') === 'password') {
                        input.attr('type', 'text');
                        icon.removeClass('fa-eye').addClass('fa-eye-slash');
                } else {
                        input.attr('type', 'password');
                        icon.removeClass('fa-eye-slash').addClass('fa-eye');
                }
        });
        
        // Form validation
        $('#signupForm').submit(function(e) {
                e.preventDefault();
                
                // Simple validation
                const password = $('#signupPassword').val();
                const confirmPassword = $('#signupConfirmPassword').val();
                
                if (password !== confirmPassword) {
                        $('#passwordMatchError').removeClass('d-none');
                        return;
                }
                
                // In a real app, you would submit to server here
                alert('Sign up successful! Redirecting to dashboard...');
                window.location.href = 'dashboard.html';
        });
        
        $('#signinForm').submit(function(e) {
                e.preventDefault();
                
                // In a real app, you would validate with server here
                alert('Sign in successful! Redirecting to dashboard...');
                window.location.href = 'dashboard.html';
        });
        
        // Add fade-in animation to elements
        $('.auth-container, .card-dashboard').addClass('fade-in');
});

