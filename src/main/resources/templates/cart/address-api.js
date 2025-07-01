<script th:inline="javascript">
  /*<![CDATA[*/
  document.addEventListener("DOMContentLoaded", function () {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");
    const addressDetailInput = document.getElementById("addressDetail");
    const shippingAddressInput = document.getElementById("shippingAddress");

    if (!provinceSelect || !districtSelect || !wardSelect) {
      return; // tránh lỗi nếu fragment chưa được render
    }

    // Load tỉnh
    fetch("https://provinces.open-api.vn/api/?depth=1")
      .then(res => res.json())
      .then(data => {
        provinceSelect.innerHTML = "<option value=''>-- Chọn Tỉnh/Thành phố --</option>";
        data.forEach(province => {
          const opt = document.createElement("option");
          opt.value = province.code;
          opt.textContent = province.name;
          provinceSelect.appendChild(opt);
        });
      });

    // Load quận/huyện
    provinceSelect.addEventListener("change", () => {
      const provinceCode = provinceSelect.value;
      districtSelect.innerHTML = "<option value=''>-- Chọn Quận/Huyện --</option>";
      wardSelect.innerHTML = "<option value=''>-- Chọn Phường/Xã --</option>";

      fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`)
        .then(res => res.json())
        .then(data => {
          data.districts.forEach(district => {
            const opt = document.createElement("option");
            opt.value = district.code;
            opt.textContent = district.name;
            districtSelect.appendChild(opt);
          });
        });
    });

    // Load phường/xã
    districtSelect.addEventListener("change", () => {
      const districtCode = districtSelect.value;
      wardSelect.innerHTML = "<option value=''>-- Chọn Phường/Xã --</option>";

      fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`)
        .then(res => res.json())
        .then(data => {
          data.wards.forEach(ward => {
            const opt = document.createElement("option");
            opt.value = ward.name;
            opt.textContent = ward.name;
            wardSelect.appendChild(opt);
          });
        });
    });

    // Gộp địa chỉ
    const checkoutForm = document.getElementById("checkoutForm");
    if (checkoutForm) {
      checkoutForm.addEventListener("submit", function (e) {
        if (!provinceSelect.value || !districtSelect.value || !wardSelect.value || !addressDetailInput.value.trim()) {
          alert("Vui lòng nhập đầy đủ địa chỉ.");
          e.preventDefault();
          return;
        }
        const fullAddress = `${addressDetailInput.value}, ${wardSelect.options[wardSelect.selectedIndex].text}, ${districtSelect.options[districtSelect.selectedIndex].text}, ${provinceSelect.options[provinceSelect.selectedIndex].text}`;
        shippingAddressInput.value = fullAddress;
      });
    }
  });
  /*]]>*/
</script>
