$(document).ready(function() {
    $('#registerForm').on('submit', function(event) {
        // ✅ XÓA event.preventDefault() để form tự gửi
        // 👉 Chỉ thêm validate frontend
        $('.error-message').text('');
        $('.form-group input').removeClass('is-invalid');

        const username = $('#username').val();
        const email = $('#email').val();
        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();

        let isValid = true;

        if (!username) {
            $('#usernameError').text('Tên người dùng không được để trống.');
            $('#username').addClass('is-invalid');
            isValid = false;
        }
        if (!email) {
            $('#emailError').text('Email không được để trống.');
            $('#email').addClass('is-invalid');
            isValid = false;
        } else if (!/\S+@\S+\.\S+/.test(email)) {
            $('#emailError').text('Email không hợp lệ.');
            $('#email').addClass('is-invalid');
            isValid = false;
        }
        if (!password || password.length < 6) {
            $('#passwordError').text('Mật khẩu phải có ít nhất 6 ký tự.');
            $('#password').addClass('is-invalid');
            isValid = false;
        }
        if (confirmPassword !== password) {
            $('#confirmPasswordError').text('Mật khẩu không khớp.');
            $('#confirmPassword').addClass('is-invalid');
            isValid = false;
        }

        // ❌ Nếu có lỗi => dừng submit
        if (!isValid) {
            event.preventDefault();
        }
    });
});


 async function sendOtp() {
    const phone = document.getElementById("phone").value;
    if (!phone) {
      alert("Vui lòng nhập số điện thoại!");
      return;
    }
    const res = await fetch(`/otp/send?phone=${encodeURIComponent(phone)}`, { method: 'POST' });
    alert(await res.text());
  }

  async function verifyOtp() {
    const phone = document.getElementById("phone").value;
    const otp = document.getElementById("otp").value;
    if (!otp) {
      alert("Vui lòng nhập OTP!");
      return;
    }
    const res = await fetch(`/otp/verify?phone=${encodeURIComponent(phone)}&otp=${encodeURIComponent(otp)}`, { method: 'POST' });
    alert(await res.text());
  }

  function sendOtp() {
    const phone = document.getElementById("phone").value;
    fetch("/otp/send?phone=" + phone, { method: "POST" })
      .then(resp => resp.text())
      .then(msg => alert(msg))
      .catch(err => alert("Lỗi: " + err));
  }