function sendOtp() {
  const phone = document.getElementById("phone").value;
  if (!phone) {
    alert("Vui lòng nhập số điện thoại trước khi gửi OTP");
    return;
  }

  fetch("/otp/send?phone=" + encodeURIComponent(phone), {
    method: "POST"
  })
    .then(response => {
      if (!response.ok) {
        throw new Error("Không gửi được OTP");
      }
      return response.text();
    })
    .then(msg => {
      alert("OTP đã được gửi đến số: " + phone);
    })
    .catch(error => {
      console.error(error);
      alert("Có lỗi xảy ra khi gửi OTP");
    });
}
