import cv2
import numpy as np
import sys
import os

def process_shirt(fabric_path, outline_path, output_path):
    # 1. Load Images
    fabric_img = cv2.imread(fabric_path)
    shirt_outline = cv2.imread(outline_path)

    if fabric_img is None or shirt_outline is None:
        print("Error: Images not found")
        return

    # Resize fabric to match shirt dimensions exactly
    h, w = shirt_outline.shape[:2]
    fabric_img = cv2.resize(fabric_img, (w, h))

    # 2. Create Masks (Logic from your script)
    gray = cv2.cvtColor(shirt_outline, cv2.COLOR_BGR2GRAY)
    _, binary = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY)

    # Floodfill to find background
    im_floodfill = binary.copy()
    mask = np.zeros((h+2, w+2), np.uint8)
    cv2.floodFill(im_floodfill, mask, (0,0), 0)

    # Interior Mask (White area is the shirt)
    interior_mask = im_floodfill

    # Outline Mask (Black area is the shirt)
    outline_mask = cv2.bitwise_not(interior_mask)

    # 3. The Blending Magic
    #

    # Cut out the shirt shape from the FABRIC
    textured_shirt = cv2.bitwise_and(fabric_img, fabric_img, mask=interior_mask)

    # Cut out the outlines from the ORIGINAL SKETCH
    background_and_outlines = cv2.bitwise_and(shirt_outline, shirt_outline, mask=outline_mask)

    # Combine them
    final_output = cv2.add(textured_shirt, background_and_outlines)

    # 4. Save
    cv2.imwrite(output_path, final_output)
    print("Success")

if __name__ == "__main__":
    # Arguments: 1=FabricPath, 2=OutlinePath, 3=OutputPath
    if len(sys.argv) < 4:
        print("Missing arguments")
    else:
        process_shirt(sys.argv[1], sys.argv[2], sys.argv[3])