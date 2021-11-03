# IceSlidePublicRepo
Public version of my Ice Slide app with private keys removed

Hi! This is the primary source code for my app "Ice Slide" 

found here:  https://play.google.com/store/apps/details?id=com.andrew.game

Here is what's in each folder

Currency: finite state machine to manage the in game currency

enums: a set of Enum types to help manages different states

interfaces: a set of interfaces used for callbacks and interfacing with Android specific code

observer: implements the Observer pattern

player: finite state machine to manage the player's movements

screens: manages the differnt screens in the game
    gameplay: set of screens for the different gameplay screens
        - Uses inheritance for code reuse purposes
        congratulations: set of congraulation screens for the different game modes
    menu: set of screens for the different parts of the main menu
    
slip:  miscelaneous files used to manage in game lives, currency, databse updates, and more

statemachines: a set of interfaces to define finite state machines used in the game

UI: defines a set of UI objects used in conjuction with the screens



