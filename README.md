# Static to CSV Converter

This is my quick and dirty project for fileconversion for the game mission files of the Digital Combat Simulator (DCS). The following is the explanation of Copilot:

Static to CSV Converter is a Java application that converts static data from `.miz` files into CSV format. The application provides a graphical user interface (GUI) for selecting input and output directories, and it ensures that the input file is named "mission". The application also handles the extraction of `.miz` files, which are essentially zip files, and processes the `mission` file contained within.

## Features

- **GUI Interface**: Easy-to-use graphical interface for selecting input and output directories.
- **File Handling**: Automatically changes the file extension of `.miz` files to `.zip` and extracts them.
- **Data Conversion**: Converts static data from the `mission` file into CSV format.
- **Executable Packaging**: Packages the application and custom JRE into a single executable using Launch4J.

## Requirements

- Java 21 or higher
- Maven 3.6.0 or higher (only for Devs)

## Installation (without using the Release)

1. **Clone the repository**:
    ```sh
    git clone https://github.com/DrSommer20/dcsStaticFromMiz.git
    cd dcsStaticFromMiz
    ```

2. **Build the project**:
    ```sh
    mvn clean package
    ```

3. **Run the application**:
    - If you want to run the application directly from the JAR file:
      ```sh
      java -jar target/statictocsv-1.0-jar-with-dependencies.jar
      ```
    - If you want to use the custom JRE and executable:
      ```sh
      target/statictocsv.exe
      ```

## Usage

1. **Launch the application**:
    - Run the [statictocsv.exe](http://_vscodecontentref_/0) file located in the [target](http://_vscodecontentref_/1) directory.

2. **Select Input Directory**:
    - Click the "Browse" button next to "Input Directory" and select the directory containing the `.miz` file.

3. **Select Output Directory**:
    - Click the "Browse" button next to "Output Directory" and select the directory where you want to save the CSV file.

4. **Convert**:
    - Click the "Convert" button to start the conversion process. The application will extract the `.miz` file, process the `mission` file, and save the output as `output.csv` in the selected output directory.
