# Problem Statement

## Motivation

Robotics competitions like FIRST LEGO League (FLL) and the World Robot Olympiad (WRO) are global educational events where students design, build, and program robots to complete specific tasks or challenges. These competitions promote hands-on learning in STEM (science, technology, engineering, and math), foster creativity and teamwork, and encourage problem-solving in real-world contexts. By participating, students gain valuable technical and interpersonal skills that prepare them for future careers and inspire innovation at an early age.

According to Hands On Technology, 64% of FLL participants showed an enlarged interest in STEM, and even 86% enhanced their valuable teamwork skills.[^1] 704 Teams participated in the 2024 FLL (in Germany, Austria, and Switzerland)[^1], 897 Teams in the WRO (in Germany) [^2]. These teams attend one of the 55 regional FLL competitions / 50 regional WRO competitions.

This project is aimed at organisers of these regional events. When organising such an event, there is a growing need for an intelligent and automated information system to enhance the overall event experience for both participants and audiences. One of our team members, Lucas, who is also an organizer of such robotic competitions in Munich, has identified a practical and pressing issue during these events: the lack of an efficient and automated way to inform the participants.
During different stages of the competition, various types of information are of interest and need to be presented — including real-time scores, leaderboards, match countdowns, team performance data, announcements, and sponsor advertisements. Some organisers already use dsipays with static images for this purpose. However, managing this content is typically done manually, which is not only time-consuming but also prone to errors and delays.
Moreover, different screens may require different content at the same time — for example, one screen might focus on live match data while another displays upcoming match schedules or promotional material from sponsors. This further complicates the manual management process.
To address this challenge, our team aims to develop a centralized scheduling and display control software that can automatically switch and manage the content shown on multiple screens based on predefined rules, real-time events, and competition phases. The system will allow seamless integration with existing competition infrastructure, support dynamic updates, and ensure that the right content is displayed on the right screen at the right time.
This project is not just an academic exercise — the final software will be deployed in actual robotic competitions, providing a real-world solution that improves efficiency, accuracy, and engagement for both organizers and participants.

## Objective

The objective of this project is to design and develop an intelligent, automated display scheduling system for robotic competitions.

# Functional Requirements

- **Registration**: The system should allow clients to register themselves as an information display.
- **Screen Overview**: The event organiser can see and manage connected clients. They can modify the name of the client for their convenience. They also see the current connection status.
- **Dynamic Content**: The system should dynamically manage and control the content shown on multiple screens throughout the event venue.
- **Appropriate Information**: The system should ensure that the appropriate information — such as real-time scores, leaderboards, match updates, and sponsor advertisements — is displayed at the correct time and on the correct screen, depending on the current time, event phase, and other information sources.
- **Reduced Workload**: The system should reduce the manual workload on event organizers, minimize human errors as much as possible.
- **Content management**: At any point, the event organiser can change the content shown to their need.
- **Screen Groups**: Screens can be grouped together, showing the same content on all screens in the group.
- **Offline Fallback**: In case of a connection loss, the screen should switch into a modifiable default view. Under no circumstances, the screen should contain wrong or outdated information.
- **Customizable Content**: Presets like leaderboards can be customised to the look wished by the event organiser.
- **Image Upload**: Additionally to the default contents, organisers can upload their own image to show on the screens.
- **AI Integration**: An AI should help the organiser by suggesting content that might be missing in the current configuration and even can create new images to show on the screen
- **Scoring Integration**: The leaderboard should contain live data from the events scoring system.

# Non-Functional Requirements

- **Real-time performance**: the system should be responsive for gaming change as fast as possible
- **Extensibility**: The operator must be able to add new content, new ranking, new rating formulas, etc.
- **Scalability**: The system must be supported by different end-devices
- **Usability** The system must be intuitive to operators. It should be easy to learn, to use and efficient.

# Scenarios

## Scenario 1: Automatic Scoring Updates

### Actors
- **Max**: Event Referee
- **Alice**, **Bob**: Audience

### Flow of Events
1. Max finishes his scoring and enters the data into the official scoring software. His scoring completes the first round of matches.
2. The system recognizes that the first round is finished and initiates a switch to the leaderboard view.
3. The connected clients recognize the switch and show the leaderboard.
4. Max clicks the **"Update Screens"** button.
5. Alice, watching from the audience, sees the updated score and finds out that her team is currently leading.
6. Max, sitting in the team area, receives the same information on a different display. He happily meets up with Alice to celebrate.



[^1]: HANDS on TECHNOLOGY e.V. - Wirkungsbericht 2023 (https://www.hands-on-technology.org/de/vision/wirkungsberichte?file=files/Dateiverwaltung%20NEU/Dokumente/Presse%20%26%20Downloads/Wirkung/Wirkungsbericht_HANDS%20on%20TECHNOLOGY_2023.pdf&cid=9519) last accessed 07.05.2025

[^2] WRO 2024 Competition Overview (https://www.worldrobotolympiad.de/saison-2024/wettbewerbe) last accessed 07.05.2025
