document.addEventListener("submit", function (event) {
  const form = event.target.closest(".ajax-cart-form");
  if (!form) return;

  event.preventDefault();
  const feedback = document.getElementById("cart-feedback");
  fetch(form.getAttribute("action"), {
    method: "POST",
    body: new URLSearchParams(new FormData(form)),
    headers: { "X-Requested-With": "XMLHttpRequest" }
  })
    .then(async function (response) {
      const contentType = response.headers.get("content-type") || "";
      if (!contentType.toLowerCase().includes("application/json")) {
        throw new Error("The server returned an unexpected response. Please refresh the page and try again.");
      }

      const data = await response.json();
      if (data.redirect) {
        window.location.assign(data.redirect);
        return null;
      }
      if (!response.ok) throw new Error(data.message || "Unable to update the cart.");
      return data;
    })
    .then(function (data) {
      if (data && data.reload) {
        window.location.reload();
      }
    })
    .catch(function (error) {
      if (feedback) {
        feedback.className = "alert alert-error";
        feedback.textContent = error.message;
      } else {
        window.alert(error.message);
      }
    });
});
