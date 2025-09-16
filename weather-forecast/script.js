const apiKey = "1532b49d6e57c29335a24f24bbf2fc2b";

function getWeather() {
  const city = document.getElementById("cityInput").value.trim();
  if (city === "") {
    alert("Please enter a city name!");
    return;
  }

  const url = `https://api.openweathermap.org/data/2.5/weather?q=${encodeURIComponent(city)}&appid=${apiKey}&units=metric`;

  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error("City not found or API error!");
      }
      return response.json();
    })
    .then(data => {
      const temperature = data.main.temp;
      const description = data.weather[0].description;
      const humidity = data.main.humidity;

      document.getElementById("city").textContent = `Weather in ${data.name}`;
      document.getElementById("temp").textContent = `Temperature: ${temperature}°C`;
      document.getElementById("desc").textContent = `Condition: ${description}`;
      document.getElementById("humidity").textContent = `Humidity: ${humidity}%`;
    })
    .catch(error => {
      console.error(error);
      document.getElementById("city").textContent = "Error: Could not fetch weather.";
      document.getElementById("temp").textContent = "";
      document.getElementById("desc").textContent = "";
      document.getElementById("humidity").textContent = "";
    });
}

// Optional: allow pressing Enter key to get weather
document.getElementById("cityInput").addEventListener("keypress", function(e) {
  if (e.key === "Enter") {
    getWeather();
  }
});
