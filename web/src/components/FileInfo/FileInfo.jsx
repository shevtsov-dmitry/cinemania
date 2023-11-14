import {useEffect, useRef, useState} from "react";


function FileInfo(props) {
    const serverUrl = "http://localhost:8080"

    const suggestionsTextHighlightColor = "#f72585"
    // getters and setters
    const [filmName, setFilmName] = useState('')
    const [country, setCountry] = useState('')
    const [releaseDate, setReleaseDate] = useState('')
    const [genre, setGenre] = useState('')
    const [minimalAge, setMinimalAge] = useState('')
    const [imageUrl, setImageUrl] = useState('')
    const [watchTime, setWatchTime] = useState('')
    const [rating, setRating] = useState('')

    // independent elements
    const formRef = useRef();

    const fieldNameArrayIndex = {
        "filmName": 0,
        "country": 1,
        "releaseDate": 2,
        "genre": 3,
        "minimalAge": 4,
        "imageUrl": 5,
        "watchTime": 6,
        "rating": 7
    }

    // position references
    const filmNameRef = useRef()
    const countryRef = useRef()
    const releaseDateRef = useRef()
    const genreRef = useRef()
    const minimalAgeRef = useRef()
    const imageUrlRef = useRef()
    const watchTimeRef = useRef()
    const ratingRef = useRef()
    const inputFieldReferencesList = [filmNameRef, countryRef, releaseDateRef, genreRef,
        minimalAgeRef, imageUrlRef, watchTimeRef, ratingRef]

    // text suggestion references
    const popupFilmNameRef = useRef()
    const popupCountryRef = useRef()
    const popupReleaseDateRef = useRef()
    const popupGenreRef = useRef()
    const popupMinimalAgeRef = useRef()
    const popupImageUrlRef = useRef()
    const popupWatchTimeRef = useRef()
    const popupRatingRef = useRef()
    const popupsReferencesList = [popupFilmNameRef, popupCountryRef, popupReleaseDateRef,
        popupGenreRef, popupMinimalAgeRef, popupImageUrlRef, popupWatchTimeRef, popupRatingRef]

    // requests data
    const [contentAssistListItems, setContentAssistListItems] = useState([])
    const [inputName, setInputName] = useState(null)
    const [inputValue, setInputValue] = useState(null)


    function fillForm(e) {
        e.preventDefault();

        const filledForm = {
            filmName: filmName,
            country: country,
            releaseDate: releaseDate,
            genre: genre,
            minimalAge: minimalAge,
            imageUrl: imageUrl,
            watchTime: watchTime,
            rating: rating
        }

    }

    const handleInputChange = (input) => {
        const {name, value} = input.target
        setInputName(name)
        setInputValue(value)

        setGlobalNameValuesForReferences()
        showTypingSuggestions(name, value)
    };

    function setGlobalNameValuesForReferences() {
        switch (name) {
            case 'filmName':
                setFilmName(value);
                break;
            case 'country':
                setCountry(value);
                break;
            case 'releaseDate':
                setReleaseDate(value);
                break;
            case 'genre':
                setGenre(value);
                break;
            case 'minimalAge':
                setMinimalAge(value);
                break;
            case 'imageUrl':
                setImageUrl(value);
                break;
            case 'watchTime':
                setWatchTime(value);
                break;
            case 'rating':
                setRating(value);
                break;
        }
    }

    const showTypingSuggestions = (name, value) => {

        if (name === "genre") {
            let url = `${serverUrl}/film-info/genre/get/many/by-sequence?sequence=`
            url = url.concat(value)
            console.log(url)
            retrieveOccurrences(url);
        } else if (name === "country") {
            let url = `${serverUrl}/film-info/country/get/many/by-sequence?sequence=`
            let countryName = ""
            if (value.length > 0) {
                countryName = value[0].toUpperCase() + value.substring(1, value.length)
            }
            url = url.concat(countryName)
            console.log(url)
            retrieveOccurrences(url)
        }
    }

    function retrieveOccurrences(url) {
        fetch(url)
            .then(response => response.json())
            .then(occurrences => {
                addPopupContentAssist(occurrences);
            })
            .catch(e => {
            })
    }

    function addPopupContentAssist(data) {
        const listItems = Object.keys(data).map((k) => (
            <button className="content-assist-popup-btn" type="submit" key={k}>
                {data[k]}
            </button>))
        setContentAssistListItems(listItems);
    }

    function changeTypingSuggestionsPopupStyle() {
        if (inputName !== null) {
            let selectedPopup = popupsReferencesList[fieldNameArrayIndex[inputName]]
            selectedPopup.current.style.display = "flex"
            highlightPopupElementTextColorWhileTyping(selectedPopup);
            autoCompleteTextFromSuggestionAndChangeSelectElementsColors(selectedPopup);
            hideTypeSuggestionsPopupWhenNotFocused(selectedPopup);
        }

        function highlightPopupElementTextColorWhileTyping(selectedPopup) {
            let length = inputValue.length
            const suggestedVariants = selectedPopup.current.children
            for (let suggestedVariant of suggestedVariants) {
                suggestedVariant.innerHTML =
                    `<span style="color: ${suggestionsTextHighlightColor};">`
                    + `${suggestedVariant.textContent.substring(0, length)}</span>`
                    + `${suggestedVariant.textContent.substring(length, suggestedVariant.textContent.length)}`;
            }
        }

        // * this method also changes DOM colors when suggested variants are selected for optimization purposes
        function autoCompleteTextFromSuggestionAndChangeSelectElementsColors(selectedPopup) {
            for (let genreNameBtn of selectedPopup.current.children) {
                genreNameBtn.addEventListener("focus", (e) => {
                    genreNameBtn.style.backgroundColor = "#2b2d42"
                    genreNameBtn.style.color = "white"

                    genreNameBtn.addEventListener("click", () => {
                        insertSuggestedTextInInput(genreNameBtn.textContent)
                        selectedPopup.current.style.display = "none"
                    })

                    function insertSuggestedTextInInput(textToAppend) {
                        let selectedInput = inputFieldReferencesList[fieldNameArrayIndex[inputName]]
                        if (selectedInput !== null && selectedInput !== undefined) {
                            // ! *******************
                            // ! *******************
                            // ! *******************
                            console.log(selectedInput);
                            selectedInput.current.value = textToAppend;
                        }
                    }

                })
                genreNameBtn.addEventListener('blur', () => {
                    genreNameBtn.style.backgroundColor = ''; // Reset the background color when focus is removed
                    genreNameBtn.style.color = "black"
                });
            }
        }


        function hideTypeSuggestionsPopupWhenNotFocused(selectedPopup) {
            for (let child of inputFieldReferencesList) {
                child.current.addEventListener("focus", () => {
                    selectedPopup.current.style.display = "none"
                })
            }
        }
    }


    useEffect(() => {
        changeTypingSuggestionsPopupStyle();
    }, [contentAssistListItems]);

    const typingSuggestions = (refName) => {
        return (<ul className="typing-suggestions-ul" ref={getPopupRef(refName)}>
                {contentAssistListItems}
            </ul>

        )

        function getPopupRef(refName) {
            switch (refName) {
                case "filmName" :
                    return popupFilmNameRef;
                case "country" :
                    return popupCountryRef;
                case "releaseDate" :
                    return popupReleaseDateRef;
                case "genre" :
                    return popupGenreRef;
                case "minimalAge" :
                    return popupMinimalAgeRef;
                case "imageUrl" :
                    return popupImageUrlRef;
                case "watchTime" :
                    return popupWatchTimeRef;
                case "rating" :
                    return popupRatingRef;
            }
        }
    }

    function form() {
        return <form onSubmit={fillForm} ref={formRef}>
            <ul className="form-ul">
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>film name</p>
                    </div>
                    <input ref={filmNameRef}
                           className="form-input"
                           type="text"
                           name="filmName"
                           value={filmName}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("filmName")}
                    </div>
                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>country</p>
                    </div>
                    <input ref={countryRef}
                           className="form-input"
                           type="text"
                           name="country"
                           value={country}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("country")}
                    </div>
                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>release date</p>
                    </div>
                    <input ref={releaseDateRef}
                           className="form-input"
                           type="text"
                           name="releaseDate"
                           value={releaseDate}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("releaseDate")}
                    </div>

                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>genre</p>
                    </div>
                    <input ref={genreRef}
                           className="form-input"
                           type="text"
                           name="genre"
                           value={genre}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("genre")}
                    </div>

                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>minimal age</p>
                    </div>
                    <input ref={minimalAgeRef}
                           className="form-input"
                           type="text"
                           name="minimalAge"
                           value={minimalAge}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("minimalAge")}
                    </div>

                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>poster</p>
                    </div>
                    <input ref={imageUrlRef}
                           className="form-input"
                           type="text"
                           name="imageUrl"
                           value={imageUrl}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("imageUrl")}
                    </div>
                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>watch time</p>
                    </div>
                    <input ref={watchTimeRef}
                           className="form-input"
                           type="text"
                           name="watchTime"
                           value={watchTime}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("watchTime")}
                    </div>
                </li>
                <li className="form-li">
                    <div className="paragraph-aligner">
                        <p>rating</p>
                    </div>
                    <input ref={ratingRef}
                           className="form-input"
                           type="text"
                           name="rating"
                           value={rating}
                           onChange={handleInputChange}/>
                    <div className="relative-structure-to-hold-type-suggestions">
                        {typingSuggestions("rating")}
                    </div>

                </li>
            </ul>
            <div className="button-aligner">
                <button className="submit-button" id="add-film-button">submit</button>
            </div>
        </form>;
    }

    return (<div className="container">
        <div className="add-film-header">add film</div>
        {form()}
    </div>)
}


export default FileInfo
