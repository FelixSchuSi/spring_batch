package muenster.bank.statementcreator.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Customer")
public class Customer {
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String ssn;
    private String emailAddress;
    private String homePhone;
    private String cellPhone;
    private String workPhone;
    private int notificationPreferences;
    private List<Long> accountIds;

    public Customer() {
    }

    public Customer(long id, String firstName, String middleName, String lastName, String address1, String address2,
                    String city, String state, String postalCode, String ssn, String emailAddress, String homePhone,
                    String cellPhone, String workPhone, int notificationPreferences, List<Long> accountIds) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.ssn = ssn;
        this.emailAddress = emailAddress;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.workPhone = workPhone;
        this.notificationPreferences = notificationPreferences;
        this.accountIds = accountIds;
    }

    public List<Long> getAccountIds() {
        return this.accountIds;
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", firstName='" + firstName + '\'' + ", middleName='" + middleName + '\''
                + ", lastName='" + lastName + '\'' + ", address1='" + address1 + '\'' + ", address2='" + address2 + '\''
                + ", city='" + city + '\'' + ", state='" + state + '\'' + ", postalCode='" + postalCode + '\''
                + ", ssn='" + ssn + '\'' + ", emailAddress='" + emailAddress + '\'' + ", homePhone='" + homePhone + '\''
                + ", cellPhone='" + cellPhone + '\'' + ", workPhone='" + workPhone + '\'' + ", notificationPreferences="
                + notificationPreferences + ", accountId=" + accountIds + '}';
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getSsn() {
        return ssn;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public int getNotificationPreferences() {
        return notificationPreferences;
    }
}
