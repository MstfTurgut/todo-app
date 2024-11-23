# To Do List Application

## Description

A to-do application that allows users to manage their tasks and lists, track progress, and handle task dependencies. This application is designed to help users stay organized and efficiently prioritize their work.


## Features

1) User Authentication
- **Register:** Create a new user account by providing a username, and password.
- **Login:** Log in with your username and password to access your to-do lists.
2) Task Management
- **Create Lists:** Organize tasks by creating multiple to-do lists.
- **Add Items:** Add tasks to each list with properties such as priority and status.
- **Update Tasks:** Edit task details such as name, description, or status.
- **Delete Items:** Remove tasks from the list if they are no longer needed.
3) Task Dependencies
- **Dependencies:** Link tasks that depend on each other. A task cannot be completed until its dependent tasks are finished.
- **Task Status:** Track the status of tasks (e.g., "In Progress", "Completed") and manage their dependencies effectively.
4) Priority Management
- **Set Priorities:** Assign priority levels to tasks such as "High", "Medium", or "Low".
- **Track Progress:** Monitor the progress of each task and update its status as needed

## Screenshots

**The following screenshots provide a visual overview of the application's UI mockup and some design decisions.**


![todo-app](https://github.com/user-attachments/assets/3f93bca5-4e3a-4868-88ba-96e6612a4679)

![tables](https://github.com/user-attachments/assets/f286ce78-06a4-4ab3-9eff-ddd69b56cd5d)


## Technologies

- Spring Boot
- PostgreSQL
- Docker
- Mockito (for unit testing)

## Setup and Run

**Prerequisites:**

  - JDK 11 or higher
  - Docker
  - Maven

**Installation:**

1) Clone the repository:

       git clone https://github.com/MstfTurgut/todo-app.git

2) Build the project with Maven:

       mvn clean install

3) Set up the Docker containers:

       docker-compose up --build

4) Access the app in your browser:

       http://localhost:8080


## Contributing

1) Fork the repository.
2) Create a new branch (git checkout -b feature-name).
3) Commit your changes (git commit -am 'Add new feature').
4) Push to the branch (git push origin feature-name).
5) Create a new Pull Request.


## License

This project is licensed under the MIT License - see the LICENSE file for details.
