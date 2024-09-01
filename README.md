# Users App

1. [About](#About)
2. [Installation](#Installation)
3. [Usage](#Usage)

## About

**This is an Android app that manages a list of users with functionality to add, delete, and edit user details.**

In this app I interacted with a RESTful API - [ReqRes API](https://reqres.in/ "ReqRes API") using Retrofit library to fetch user data, and Room database to store it locally.
The app provides a user-friendly interface that follows Material Design guidelines to ensure a modern and intuitive user experience.

The application follows the Model-View-ViewModel (MVVM) architecture to ensure a clean separation of concerns.

## Technical Specifications

* Programming Language: Java
* Architecture: MVVM
* Networking Library: Retrofit
* Database Library: Room

## Installation

1. **Clone the Repository**

   ```
   git clone https://github.com/morsimantov/Users-Application.git
   ```

2. **Open the Project**
  * Open Android Studio and select "Open an existing project."
  * Navigate to the cloned project directory and open it.


3. **Build and Run**
  * Ensure you have an emulator or a physical device connected.
  * Click on the "Run" button in Android Studio to build and run the application.

## Usage

### Users List

Upon launching the app, it fetches user data from the ReqRes API and displays it in a list.
The data is then saved in the local database.

<img src="https://github.com/user-attachments/assets/dbc23f2b-17bc-45fe-ad10-eaf13bc57743" height="560" />
<br /> 
<br /> 
<br /> 

To ensure that the user data is retrieved from the API only once during the initial launch, a boolean
flag in Shared Preferences is used to track the initialization status of the database.
<br /> I assumed that you want to retrieve the users from the API once when the app is launched and then manage them in
the local database for subsequent operations to keep consistency.
<br />

### User Details

When a user is selected from the Users List screen, the app navigates to a detailed view screen that presents the user's information. This screen provides an overview of the user's details.
You can also edit and delete a user from this screen.

<img src="https://github.com/user-attachments/assets/86ed88e9-4d82-4d7b-9419-1e892cdcc08d" height="560" />

### Add a User

Navigate to the Add User screen through the floating add button on the bottom right of the User list Screen. Enter details for a new user and add it to the list.

<img src="https://github.com/user-attachments/assets/9fe39428-9676-45f6-ba60-a52dd36863ef" height="560" />
<br /> 
<br /> 

> [!NOTE]
> It is not possible to add a user with an already existing email. In this case an appropriate message will pop.

### Delete User

Use the delete icon button to remove a user from the list. For easy access, the delete and edit buttons appear on the list screen besides each user.

<img src="https://github.com/user-attachments/assets/acf5c898-18c2-44f6-8ed8-16bf0bad2e02" height="560" />
<br /> 
<br /> 
Additionally, you can delete a user through the User Details screen once you click on a user.
Click on the three dots on the top bar and then an option to delete user will appear.
<br /> 
<br /> 
<img src="https://github.com/user-attachments/assets/6d35326c-e18c-4662-8158-066d598a9b4c" height="560" />

### Update User

Use the edit icon button to update a user. A fragment will pop with a form to change the user details including their avatar.
You can also select a user from the list and click the edit floating button to update their details.

<img src="https://github.com/user-attachments/assets/50d88474-2f04-45de-b390-8d3b2f9a2f0e" height="560" />
<br /> 
<br /> 

> [!NOTE]
> In the edit screen it is also not possible edit an email with an existing user's email.

### Additional Features

- **Pagination:** The RecyclerView and Room database support pagination to handle large data sets efficiently.
- **Error Handling:** Proper error messages are displayed in a snackbar if API requests fail or due to other errors, including input validation.
- **Splash Screen:** a splash screen is shown before the app launches, enhancing the user experience.
- **Transition Animations:** Transition animations between activities, and animated buttons and operations (like delete).
