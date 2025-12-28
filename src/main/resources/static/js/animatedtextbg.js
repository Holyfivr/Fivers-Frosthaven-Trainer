
const c = document.getElementById("c");
const ctx = c.getContext("2d");
const img = new Image();
img.src = "../img/art/bg.png";

let x = 0;

img.onload = () => {
  setInterval(() => {
    ctx.clearRect(0,0,c.width,c.height);

    ctx.save();
    ctx.font = "900 78px 'Arial Black', Gadget, sans-serif";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";

    ctx.drawImage(img, x, 0, 1600, 350);
    ctx.globalCompositeOperation = "destination-in";
    ctx.fillText("Fivers Frosthaven Trainer", 700, 175);
    ctx.restore();

    x -= 0.3;
    if (x < -200) x = 0;
  }, 16);
};