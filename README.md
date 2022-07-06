# Customer-Scheduler
A JavaFX application that uses a PostgreSQL database. 
This application pulls users, customers, appointments, contacts, countries and first-level divisions from the database.
User activity on failed or successful login attempts are logged in a text document. 
Users must log-in to add, edit or delete customer or appointment information.
Customers have appointments linked to them with scheduled times. Before a customer is deleted all appointments must be deleted as well.
There is error checking on all inputs and selections with appropriate alert pop-ups.
Tables that display customer an appointment information are updated in real time. 
A reports page that displays various report lists can be accessed from the main menu.
If an appointment is scheduled within 15 mintues of login, a pop-up alter displays in the users local time.
There are language options for French on the login screen for prompts, buttons and UI elements. These are not use controlled and are based on locale. 
