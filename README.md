# Hospital Maternity Web Service API Project Instructions

## 1. Project Overview
This project focuses on the hospital maternity web service API, aiming to provide data support for hospital management and related services. By building a web service API and a desktop application, it realizes the functions of querying and managing patient admission information. Comprehensive software testing has also been carried out to ensure the quality and stability of the system.

## 2. Technology Stack
1. **Backend**: The Java Spring Boot framework is used to build the web service API, which is responsible for handling business logic and data interaction. Spring Boot provides the ability for rapid development and integration of various functions, making the development process more efficient.
2. **Frontend**: Java Swing UI is adopted to develop the desktop application, providing users with an intuitive graphical operation interface for easy interaction with the system.
3. **Testing**: JUnit 5 is used for unit testing, Mockito for creating mock objects, JaCoCo for statistical code coverage, and TestRestTemplate for sending HTTP requests in integration testing and system testing. Through these tools, the quality of the software is comprehensively guaranteed.
4. **Version Control**: Git is used for version management, and the project code is hosted on GitHub, facilitating team collaboration in development and code maintenance.
5. **Continuous Integration**: GitHub Actions is configured to achieve continuous integration. Each code push automatically triggers the test process, enabling timely detection of code issues.

## 3. Function Introduction
1. **Web Service API Functions**
    - **List patients who have never been admitted**: Through a specific endpoint, the system can filter out and return the information of patients who have never been admitted from the database.
    - **List patients readmitted within 7 days after discharge**: Based on the admission and discharge records of patients, accurately search for and return the list of patients readmitted within 7 days.
    - **Determine the month with the most admissions**: Analyze all admission records, count the number of admissions per month, and find the month with the largest number of admissions.
    - **List patients cared for by multiple medical staff**: Based on the association information between patients and medical staff, identify and return the information of patients cared for by multiple medical staff.
2. **Desktop Application Functions**: Connect to one of the endpoints of the web service API. Users can interact with the data through the graphical interface of the desktop application, such as viewing specific patient information and obtaining statistical data.

## 4. Installation and Operation
1. **Backend (Web Service API)**
    - Clone the GitHub repository of this project to your local machine: `git clone [repository link]`
    - Enter the backend directory of the project and use Maven to install dependencies: `mvn install`
    - Start the Spring Boot application: `mvn spring-boot:run`
2. **Frontend (Desktop Application)**: After the backend service is started, run the main class of the desktop application to launch the graphical interface. For specific operations, please refer to the relevant documents or code comments in the project.

## 5. Testing Situation
1. Comprehensive testing has been carried out, including unit testing, integration testing, system testing, and usability testing.
2. Unit testing ensures the correctness of each code unit. By simulating external dependencies, the accuracy and efficiency of testing are improved.
3. Integration testing verifies the correctness of interactions and data transfer between modules, ensuring that all parts of the system work together.
4. System testing simulates real - world scenarios from the overall system level, testing the system's functions, performance, and security to ensure stable operation.
5. Usability testing collects user feedback, optimizes the interface design and operation process, and enhances the user experience.

## 6. Code Structure
The project code has a clear structure, mainly divided into two major parts: backend code and frontend code. The backend code includes functional modules such as business logic implementation, data processing, and API endpoint definition. The frontend code is responsible for building the user interface and realizing data interaction and display with the backend. The specific code structure can be viewed in the project directory, and each module and class has corresponding comments to explain its functions and usage.
