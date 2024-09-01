# Users App

1. [About](#About)
2. [Installation](#Installation)
3. [Usage](#Usage)

## About

**This is an Android app that manages a list of users with functionality to add, delete, and edit user details.**

In this app I interacted with a RESTful API [ReqRes API](https://reqres.in/ "ReqRes API") using Retrofit library to fetch user data, and Room database to store it locally. 
The app provides a user-friendly interface that follows Material Design guidelines to ensure a modern and intuitive user experience.

The application follows the Model-View-ViewModel (MVVM) architecture to ensure a clean separation of concerns. 

### Technical Specifications

* Programming Language: Java
* Architecture: MVVM
* Networking Library: Retrofit
* Database Library: Room

### Installation

1. **Clone the Repository**

   ```
   git clone https://github.com/your-username/your-repo-name.git
   ```
   
2. **Open the Project**

* Open Android Studio and select "Open an existing project."
* Navigate to the cloned project directory and open it.

3. **Build and Run**
 
* Ensure you have an emulator or a physical device connected.
* Click on the "Run" button in Android Studio to build and run the application.

### Usage

1. **Users List**

Upon launching the app, it fetches user data from the ReqRes API and displays it in a list.
The data is then saved in the local database.

To ensure that the user data is retrieved from the API only once during the initial launch, a boolean 
flag in Shared Preferences is used to track the initialization status of the database.

I assumed that you want to retrieve the users from the API once when the app is launched and then manage them in
the local database for subsequent operations to keep consistency.

<img src="" height="560" />

2. **Add a User**

Navigate to the "Add User" through the floating add button to enter details for a new user and add them to the list.
It is not possible to add a user with an existing email; in this case an appropriate message will pop.

3. **Delete User**

Use the delete icon button from the users list to remove a user from the list. Additionally, you can delete a user
through the User Details screen once you click on a user:

Click on the three dots on the to bar and then an option to "Delete User" will appear.

4. **Update User**

Use the edit icon button to update a user. A fragment will pop with a form to change the user details including their avatar.
Once again, you can't edit an email with an existing user's email.
You can also select a user from the list and click the edit floating button to update their details.

### Additional Features

- **Pagination:** The RecyclerView supports pagination to handle large data sets efficiently.
- **Error Handling:** Proper error messages are displayed if API requests fail.
- **Splash Screen:** A splash screen is shown before the app launches, enhancing the user experience.
- **Transition Animations:** Transition animations between activities and animated buttons.
- **User Details screen:** A separate screen to present the user details, with CRUD operations available both in the user list and on the User Details screen.


