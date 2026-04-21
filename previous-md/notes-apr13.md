April 13 notes

Current observation:
- The current app is blank in the sense that there is no meaningful project box, tile, or productive workflow inside the app yet.

Primary requirement:
- First we need a good login authentication flow for the application.
- Use the reference app at `/home/vatsal1/Documents/g4/base-pelli/mobile-backend-android` to learn how the codebase is structured, and how login, register, onboarding, and related flows are handled there.
- In HawkFranklin we need only one kind of user authentication flow, not separate patient and doctor roles.

Desired top-level app experience:
- The first ideal page is what we currently see.
- On the top right there should be a login button.
- That login button should not be too large; it should be more like a circular profile button.
- When clicked, a window or block should appear from the left, similar to Instagram.
- Inside that panel there should be settings and login options.
- Once login is clicked, the app should go to authentication.
- Authentication can be Google authentication, or username and password.
- It should also support first-time user registration and forgot-password flows.

Expectation for the work:
- The duty is not only to design this much, but the complete user experience.
- The whole experience should feel complete.
- It should be production-ready to use.
- It should have all features expected of a normal app.

Home screen / first screen content:
- On the current home or first screen, this is our company’s internal app.
- We should show ongoing projects as square tiles with logos and project names.
- Create at least two project tiles.
- One should be `Derma AI`.
- The other should be `Telemedicine`.

Project tile interaction:
- When someone clicks either project tile, it should show a popup on the project description.
- That popup is ideally the research consent.
- You might notice similar consent behavior in the Pelliscope reference app.
- After pressing the agree button:
  - if the person has not authenticated, take them to the login page
  - if they have authenticated, take them to the questionnaire

Questionnaire / project experience:
- Think of this application as flashcards or questionnaires shown to doctors or clinicians.
- It is like case studies are given.
- Think of it like a test-prep style application.
- A question comes with information.
- Sometimes it also includes images, for example a skin lesion.
- The doctor or user has to answer or select an option.
- Sometimes it will be multiple choice from A to D.
- Sometimes a value is asked.
- Sometimes a text answer is acceptable.
- Then it goes to review and next.

Data and backend expectations:
- Internally, we should keep a mark and count of the question answers.
- All things should be reported or collected on Firebase.
- Projects keep changing or updating.
- Overall this should become a good data-collection app.

Requested tasks for today:
- First task: make this `notes-apr13.md` file and write all this down.
- Second task: go through the codebase and verify that we do not currently have these functionalities, identify where they can be added, and make a plan.
- No need to code those features right now.
