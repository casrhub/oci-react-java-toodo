
---

# oci-react-samples
A repository for full stack Cloud Native applications with a React JS frontend and various backends (Java, Python, DotNet, and so on) on the Oracle Cloud Infrastructure.

![image](https://user-images.githubusercontent.com/7783295/116454396-cbfb7a00-a814-11eb-8196-ba2113858e8b.png)
  

## MyToDo React JS
The `mtdrworkshop` repository hosts the materials (code, scripts, and instructions) for building and deploying a Cloud Native Application using a Java/Helidon backend.


### Requirements
The lab executes scripts that require the following software to run properly (all of which are already installed on and included with the OCI Cloud Shell):
* oci-cli
* python 2.7^
* terraform
* kubectl
* mvn (Maven) 

## How to Run This Project Locallyy

1. **Clone the repository**:
   ```bash
   git clone https://github.com/casrhub/oci-react-java-toodo
   ```
2. **Check out your desired branch**:
   ```bash
   cd oci-react-java-toodo
   git checkout <branch-name>
   ```
3. **Navigate to the MtdrSpring directory**, then go to the `backend` folder:
   ```bash
   cd MtdrSpring
   cd backend
   ```
4. **Configure your wallet path** in `application.properties`:

   Locate `spring.datasource.url=jdbc:oracle:thin:@reacttodos1d91_high?TNS_ADMIN=<your_wallet_path>` and replace `<your_wallet_path>` with the actual path to your `wallet_production`. For example:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@reacttodos1d91_high?TNS_ADMIN=/Users/example/Documents/Programming/OracleProject/oci-react-java-toodo/wallet_production
   ```
   *(Make sure to adapt the path to your system.)*

5. **Build the project** using Maven:
   ```bash
   ./mvnw clean package
   ```
6. **Run the application** locally:
   ```bash
   java -jar target/*.jar
   ```

Once the application is running, you should be able to access it at the specified port (by default, usually port 8080 unless otherwise configured in the application properties).

---

## Expect more ...
Stay tuned for additional documentation, features, and sample applications. Feel free to open an issue or submit a pull request to contribute!