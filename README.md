# ImageApp

This app allows the user to visualize an image and apply several modifications to it.

Disclaimer: 
/!\ The Stack has no limitation of size which is a problem, it can cause memory leaks.
We know that this is a problem and we implemented a solution to fix it, but this solution has not been tested enough yet, so we decided to keep it in commentary. Uncommenting the lines 187 and 192 in ImageProcesser will activate it.
The fix merges the root layer with the first layer of modification and removes the first layer. It will then only allow a limited amount of undo operations.

/!\ Rotation is bugged, it is better called first, before any other modification, it can cause problems to use it after and can create collisions with undo.


General elements: 
Buttons in the toolbar (left of the screen) -> click to show the tools corresponding to this category in the scrollbar (right of the toolbar), and click again to hide these tools. There are four categories : Draw, Resize, Rotate/Flip and Filter.
Note that if no image has been opened, the tools will not show.

While a category of tools is displayed right of the toolbar, the user can grow or reduce the size of this zone compared to the zone where the image is displayed, by clicking on the separator between these two zones

Image in the image view (right of the screen) :
When it is allowed (see drawing tools), the user can click on the bottom right corner of the displayed image to resize it manually, while keeping its size ratio
They can also click on the top side of the image to move it in the image view

"UNDO" button in the toolbar -> Can cancel some of the operations. The "UNDO" button is mentioned in the description of the cancelable operations.
Note that if the "UNDO" button is selected after certain operations and that it makes a white background appear, resizing the image will revert the dimensions of the image to its previous ones (without the white background).


MenuBar: Files:
Open: Open an image in the application from a file. This file can be a png, jpg, jpeg or gif, but the user should choose the file format in the filechooser interface.

Save as: Saves an image in a file selected by the user. The image can be saved with the formats png, jpg, jpeg or gif, but the gif format does not save an animation


ToolBar categories:

General:
When clicking on a button displayed in the scrollbar, it will show the UI elements associated with this option and hide the others of this scrollbar. When a selected button is clicked again, these elements will be hidden.
Note that if an option is selected in the scrollbar and another category is chosen in the toolbar, the option will still be available even if it is no longer visible. For example, if the draw button is selected in the Drawing scrollbar, and then the scrollbar is changed for the Resize scrollbar, the user can still draw on the image.

To move the image, click on the top left corner of it and drag it using the black dragging canvas
To resize the image, click on the bottom right corner of the image and use the resize black canvas, note that resizing is restricted on the right side so if you encounter problems related to resizing , consider moving it to the left side of your window.


Drawing tools:
Canvas: The color that will be used to draw is displayed on the squared canvas at the top

Color picker: Allow the user to choose a color (from a list, or to create their own color if they click on personalised color). It also allows to save a personalised color for the duration of the run of the app (this color disappear when the app is closed)
=> It is the existing color picking tool from the javafx library

Eyedropper: Allows to take a color from a point in the image and change the drawing color accordingly.
Note that, while the eyedropper is activated, the user can’t resize or move the image from the imageview. They should click on the eyedropper button again to deactivate it.

Draw: Allows to draw on the image with the color displayed on the canvas.
The size of the line is defined by the slider under this button.
Note that, while the draw button is activated, the user can’t resize or move the image from the imageview. They should click on the draw button again to deactivate it.
Can be cancelled with the “UNDO” button.

Shapes: Allows to insert a given shape in the image.
The color of this shape is the same as the drawing color.
Its position and size can be parametered with the textFields displayed under the shape buttons.
The shapes allowed are a rectangle, a square, an oval and a circle.
The user can also insert an image with this position and size with the button “Add image”. This button will ask the user to choose an image from its files, and then add this image to the displayed image in the imageView.
If the X or Y positions are larger than the corresponding size of the image in the imageView, the user will be informed that the operation is not possible.
If the width or height are too high for the shape to be entirely displayed on the image, the size will be reduced to the maximum that allows the shape to be entirely drawn in the image.
Can be cancelled with the “UNDO” button


Resize tools:
The width and height labels display the current size of the image in the imageView (number of pixels), and are updated every time this size changes, either from the resize tools or directly from the image view

Resize button: Allows to resize the image while keeping its dimension ratio. The user can choose the new width of the image with the slider or the textField.

Crop: Allows the user to reduce the size of the image by keeping only a part of it.
When this button is clicked, the user can choose how to resize the image on the image view. They can reduce the size of the image by clicking on the bottom right corner of the cadre, and move this cadre by clicking on its upper left corner.
When the resize is over, the user can click on the “Confirm” button to apply the changes.
If the user wants to annulate the crop operation, they can push the escape key.
Can be cancelled with the “UNDO” button (but the size stays the same)
-The image can not be moved and resized by mouse drgging while the Drawing option is active, you need to turn it off by clicking on the Draw button.

Zoom: The user can zoom or unzoom the image of a given level, and its size will change accordingly.
Note that this operation will be cancelled if the image is resized after.

Seam carving: This will apply the seam carving algorithm to the image, removing the required number of pixels to reach the entered dimensions. The removed pixels will be the ones that are the least important in the image (the ones whose color is the closest to their neighbours). This algorithm can malfunction when the required dimensions are much smaller than the original dimensions or greater than the original dimensions. In that case, the user is asked to enter different dimensions. Can be (somehow) cancelled by the "UNDO" button.


Rotate/Flip:
Rotate: The user can rotate the image displayed by an angle selected with the slider. This will grow the size of the image and add a white part to keep a rectangular shape.
Note that this feature is unfinished and can create many bugs when combined with the other features.
Can be (sometimes) cancelled with the ”UNDO” button
It can be completely disabled by mistake in some cases, so it must be used with caution preferably used first before any other modification.

Flip: The user can flip the image vertically or horizontally.
Can be cancelled with the “UNDO” button


Filter:
There is only one filter, the mosaic filter
This feature allows the user to apply the filter on the image using the mosaic algorithm.
The user can choose the number of points for this algorithm with the textField, then start the algorithm with the “Start” button.
If the number of points is greater than the number of pixels of this image displayed in the label above, a message will inform the user that the operation is impossible.
Note that, if the image is resized from the imageView while the mosaic filter is selected, the number of pixels will stay unchanged. If the number of pixels becomes smaller than the number of points, the algorithm won’t start, but no error message will be displayed.
Can be cancelled with the “UNDO” button

