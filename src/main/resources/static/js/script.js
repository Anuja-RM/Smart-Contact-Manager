// CHANGE THEME
let currentTheme=getTheme();

//intially
document.addEventListener("DOMContentLoaded", () =>{
    changeTheme();
});

//change theme
function changeTheme(){
    //set to webpage
    changePageTheme(currentTheme,"");

    //set the listener to change theme button
    const changeThemeButton= document.querySelector("#theme_change_button");
    //changing theme
    changeThemeButton.addEventListener("click", (event) => {
        let oldTheme = currentTheme;
        if (currentTheme === "dark"){
            //change to light
            currentTheme = "light";
        }else {
            //change to dark
            currentTheme = "dark";
        }

        changePageTheme(currentTheme,oldTheme);
    });
}

//set theme to local storage
function setTheme(theme){
    localStorage.setItem("theme",theme);
}

//get theme from local storage
function getTheme() {
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light"
}

//change page theme
function changePageTheme(theme,oldTheme){
    //update in local storage
    setTheme(currentTheme);
    //remove the current theme
    if(oldTheme){
        document.querySelector("html").classList.remove(oldTheme);
    }
    //set the current theme
    document.querySelector("html").classList.add(theme);
    //change the text in the button
    document.querySelector("#theme_change_button").querySelector('span').textContent = theme == "light" ? "Dark" : "Light";
}
// END OF CHANGE THEME