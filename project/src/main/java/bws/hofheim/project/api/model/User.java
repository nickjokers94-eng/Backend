package bws.hofheim.project.api.model;

public class User {

    public int userID;
    public String username;
    public String role;
    public String status;

    public User(int userID, String username, String role, String status){
        this.userID = userID;
        this.username = username;
        this.role = role;
        this.status = status;
    }
    public User(){}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
