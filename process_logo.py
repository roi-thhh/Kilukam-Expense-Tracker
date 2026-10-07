import numpy as np
from PIL import Image

def process_logo():
    # Load the image
    img = Image.open(r"C:\Users\12345\.gemini\antigravity-ide\brain\0a0a305e-0871-4938-8ee0-eb733b18c2de\.user_uploaded\media_1791398418285.png").convert("RGB")
    
    # Convert to numpy array
    arr = np.array(img, dtype=np.float32)
    
    # Calculate luminance
    luma = 0.299 * arr[:,:,0] + 0.587 * arr[:,:,1] + 0.114 * arr[:,:,2]
    
    # Find min (red) and max (white) luma
    # Since it's mostly flat colors, we can use percentiles to ignore noise/compression artifacts
    luma_min = np.percentile(luma, 5)
    luma_max = np.percentile(luma, 95)
    
    # Normalize luma to 0-1
    normalized = (luma - luma_min) / (luma_max - luma_min)
    normalized = np.clip(normalized, 0, 1)
    
    # Define our colors
    # Purple: (55, 43, 59) -> for the red parts (luma 0)
    # Orange: (255, 99, 49) -> for the white parts (luma 1)
    color_purple = np.array([55, 43, 59], dtype=np.float32)
    color_orange = np.array([255, 99, 49], dtype=np.float32)
    
    # Blend colors
    # Add an axis to normalized so it broadcasts to (H, W, 3)
    norm_3d = normalized[..., np.newaxis]
    result = color_purple * (1 - norm_3d) + color_orange * norm_3d
    
    # Save result
    result_img = Image.fromarray(result.astype(np.uint8))
    result_img.save("app/src/main/res/drawable/kilukkam_logo.png")
    
    # Also save an icon version (e.g., scaled down and square padded)
    size = max(result_img.size)
    icon = Image.new("RGB", (size, size), (55, 43, 59))
    icon.paste(result_img, ((size - result_img.size[0]) // 2, (size - result_img.size[1]) // 2))
    icon = icon.resize((512, 512), Image.Resampling.LANCZOS)
    icon.save("app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
    icon.save("app/src/main/res/mipmap-xxhdpi/ic_launcher.png")
    icon.save("app/src/main/res/mipmap-xhdpi/ic_launcher.png")
    icon.save("app/src/main/res/mipmap-hdpi/ic_launcher.png")
    icon.save("app/src/main/res/mipmap-mdpi/ic_launcher.png")
    
    # Same for ic_launcher_round
    # Create a circular mask
    mask = Image.new("L", (512, 512), 0)
    from PIL import ImageDraw
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, 512, 512), fill=255)
    icon_round = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    icon_round.paste(icon, (0, 0), mask=mask)
    icon_round.save("app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png")
    icon_round.save("app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png")
    icon_round.save("app/src/main/res/mipmap-xhdpi/ic_launcher_round.png")
    icon_round.save("app/src/main/res/mipmap-hdpi/ic_launcher_round.png")
    icon_round.save("app/src/main/res/mipmap-mdpi/ic_launcher_round.png")

if __name__ == "__main__":
    process_logo()
