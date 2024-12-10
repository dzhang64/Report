# ReportHelper

## Description

ReportHelper is a Java-based application designed to generate detailed LTE test reports for Jiangsu Unicom. The application uses various libraries to process and visualize data, including Apache POI for Excel manipulation, MySQL for database interactions, and several other utilities for image processing and data handling.

## Features

- Generate detailed LTE test reports
- Process and visualize CQT and DT data
- Insert images and charts into Excel reports
- Handle various data formats including CSV and Excel
- Support for multiple dependencies and utilities

## Technologies Used

- Java
- Maven
- JavaFX
- Apache POI
- MySQL
- Thumbnailator
- Metadata Extractor
- OpenCSV
- Jsoup
- Commons HttpClient
- Geodesy
- jSerialComm

## Prerequisites

- Java 8 or higher
- Maven
- MySQL

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/dzhang64/Report.git
    ```
2. Navigate to the project directory:
    ```sh
    cd ReportHelper
    ```
3. Install the dependencies:
    ```sh
    mvn install
    ```

## Usage

1. Configure the database connection in `src/main/resources/application.properties`.
2. Build the project:
    ```sh
    mvn package
    ```
3. Run the application:
    ```sh
    mvn javafx:run
    ```

## Project Structure

- `src/main/java/report/controller/ManagerController.java`: Handles the UI interactions and user management.
- `src/main/java/report/functions/jsunicom/lte/Report.java`: Generates the LTE test reports.
- `pom.xml`: Maven configuration file with project dependencies.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## Acknowledgements

- [Apache POI](https://poi.apache.org/)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
- [Thumbnailator](https://github.com/coobird/thumbnailator)
- [Metadata Extractor](https://github.com/drewnoakes/metadata-extractor)
- [OpenCSV](http://opencsv.sourceforge.net/)
- [Jsoup](https://jsoup.org/)
- [Commons HttpClient](https://hc.apache.org/httpclient-legacy/)
- [Geodesy](https://github.com/chrisveness/geodesy)
- [jSerialComm](https://fazecast.github.io/jSerialComm/)
