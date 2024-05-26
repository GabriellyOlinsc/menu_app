## Project Overview 
This project is a mobile application developed in Java using Android Studio.
It aims to present the menu of a restaurant, including dishes, drinks, desserts, etc., 
with each menu item displaying its name, price, and an image.
The app fetches data from a json local file and saves it locally using SQLite for structured data
and the file system for images. 
If the remote service is unavailable, the app will present the locally stored data.

### Objectives
The primary objective of this project is to create an aesthetically pleasing mobile application that can display a restaurant's menu. The application should:

1. Display the restaurant menu on the home screen, including the name, price, and image of each item.
2. Ensure that the presentation of data is visually appealing.
3. Save all fetched data locally using SQLite for structured data and the file system for images, to be able to present the data even when offline.
4. If the remote service is unavailable, display the locally stored data, including images, and show "to be consulted" instead of the price when offline.
   

This project explores the concepts of reading external and local data sources, emphasizing robust data handling and user experience in an offline-first mobile application.
