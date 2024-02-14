import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'

function FormAddFilm() {
    // *** ASSIGNMENT
    const serverUrl = 'http://localhost:8080'
    const suggestionsTextHighlightColor = '#f72585'
    // getters and setters
    const [filmName, setFilmName] = useState('')
    const [country, setCountry] = useState('')
    const [releaseDate, setReleaseDate] = useState('')
    const [genre, setGenre] = useState('')
    const [minimalAge, setMinimalAge] = useState('')
    const [imageUrl, setImageUrl] = useState('')
    const [videoUrl, setVideoUrl] = useState('')
    const [watchTime, setWatchTime] = useState('')
    const [rating, setRating] = useState('')

    // independent elements
    const formRef = useRef()

    // position references
    const filmNameRef = useRef()
    const countryRef = useRef()
    const releaseDateRef = useRef()
    const genreRef = useRef()
    const minimalAgeRef = useRef()
    const imageUrlRef = useRef()
    const videoUrlRef = useRef()
    const watchTimeRef = useRef()
    const ratingRef = useRef()

    const inputFieldReferencesList = [
        filmNameRef,
        countryRef,
        releaseDateRef,
        genreRef,
        minimalAgeRef,
        imageUrlRef,
        videoUrlRef,
        watchTimeRef,
        ratingRef,
    ]
    const [focusedReference, setFocusedReference] = useState(null)

    // text suggestion references
    const popupFilmNameRef = useRef()
    const popupCountryRef = useRef()
    const popupReleaseDateRef = useRef()
    const popupGenreRef = useRef()
    const popupMinimalAgeRef = useRef()
    const popupImageUrlRef = useRef()
    const popupWatchTimeRef = useRef()
    const popupRatingRef = useRef()
    const popupsReferencesList = [
        popupFilmNameRef,
        popupCountryRef,
        popupReleaseDateRef,
        popupGenreRef,
        popupMinimalAgeRef,
        popupImageUrlRef,
        popupWatchTimeRef,
        popupRatingRef,
    ]
    const [focusedPopup, setFocusedPopup] = useState(null)
    // requests data
    const [retrievedSuggestions, setRetrievedSuggestions] = useState([])
    const [inputName, setInputName] = useState(null)
    const [inputValue, setInputValue] = useState(null)

    function setFocusedEventListenerForEachInputElement() {
        for (let inputField of inputFieldReferencesList) {
            inputField.current.addEventListener('focus', () => {
                setFocusedReference(inputField)
            })
        }
    }

    function boundSuggestionsPopupToFocusedTextInput() {
        if (focusedReference == null) {
            return
        }
        const index = inputFieldReferencesList.indexOf(focusedReference)
        const popup = popupsReferencesList[index]
        setFocusedPopup(popup)
    }

    function fillContentAssistList(promise) {
        promise
            .then((response) => response.json())
            .then((data) => {
                const listItems = Object.keys(data).map((k) => (
                    <button
                        className="content-assist-popup-btn"
                        type="submit"
                        key={k}
                    >
                        {data[k]}
                    </button>
                ))
                setRetrievedSuggestions(listItems)
            })
            .catch((e) => {
                console.error('Error fetching or parsing data:', e)
            })
    }

    function retrieveMatches(name, value) {
        if (name === 'genre') {
            let url = `${serverUrl}/film-info/genre/get/many/by-sequence?sequence=`
            url = url.concat(value)
            return fetch(url)
        } else if (name === 'country') {
            let url = `${serverUrl}/film-info/country/get/many/by-sequence?sequence=`
            let countryName = ''
            if (value.length > 0) {
                countryName =
                    value[0].toUpperCase() + value.substring(1, value.length)
            }
            url = url.concat(countryName)
            return fetch(url)
        }
        return Promise.resolve(null)
    }

    function highlightPopupElementTextColorWhileTyping() {
        let length = inputValue.length
        const suggestedVariants = focusedPopup.current.children
        for (let suggestedVariant of suggestedVariants) {
            suggestedVariant.innerHTML =
                `<span style="color: ${suggestionsTextHighlightColor};">` +
                `${suggestedVariant.textContent.substring(0, length)}</span>` +
                `${suggestedVariant.textContent.substring(
                    length,
                    suggestedVariant.textContent.length
                )}`
        }
    }

    function hideTypeSuggestionsPopupWhenNotFocused() {
        for (let child of inputFieldReferencesList) {
            child.current.addEventListener('focus', () => {
                focusedPopup.current.style.display = 'none'
            })
        }
    }

    function displayOrHideSuggestionsBlock(suggestion) {
        suggestion.addEventListener('focus', (e) => {
            suggestion.style.backgroundColor = '#2b2d42'
            suggestion.style.color = 'white'
        })
        suggestion.addEventListener('blur', () => {
            suggestion.style.backgroundColor = '' // Reset the background color when focus is removed
            suggestion.style.color = 'black'
        })
        suggestion.addEventListener('click', () => {
            focusedPopup.current.style.display = 'none'
        })
    }

    function autoCompleteSuggestionOnClick(suggestion) {
        suggestion.addEventListener('click', () => {
            focusedReference.current.value = suggestion.textContent
        })
    }

    // initialization
    useEffect(() => {
        setFocusedEventListenerForEachInputElement()
    }, [])

    useEffect(() => {
        boundSuggestionsPopupToFocusedTextInput()
    }, [focusedReference])

    // popups change
    useEffect(() => {
        if (focusedPopup == null || retrievedSuggestions == null) {
            return
        }
        focusedPopup.current.style.display = 'flex'
        for (let suggestion of focusedPopup.current.children) {
            displayOrHideSuggestionsBlock(suggestion)
            autoCompleteSuggestionOnClick(suggestion)
        }
        highlightPopupElementTextColorWhileTyping()
        hideTypeSuggestionsPopupWhenNotFocused()
    }, [retrievedSuggestions])

    const typingSuggestions = (refName) => {
        return (
            <ul className="typing-suggestions-ul" ref={getPopupRef(refName)}>
                {retrievedSuggestions}
            </ul>
        )
    }

    function handleInputChange(input) {
        const { name, value } = input.target
        setInputName(name)
        setInputValue(value)
        setGlobalNameValuesForReferences(name, value)
        const promisedData = retrieveMatches(inputName, inputValue)
        fillContentAssistList(promisedData)
    }

    function getPopupRef(refName) {
        switch (refName) {
            case 'filmName':
                return popupFilmNameRef
            case 'country':
                return popupCountryRef
            case 'releaseDate':
                return popupReleaseDateRef
            case 'genre':
                return popupGenreRef
            case 'minimalAge':
                return popupMinimalAgeRef
            case 'imageUrl':
                return popupImageUrlRef
            case 'watchTime':
                return popupWatchTimeRef
            case 'rating':
                return popupRatingRef
            default:
                return ''
        }
    }

    function setGlobalNameValuesForReferences(name, value) {
        switch (name) {
            case 'filmName':
                setFilmName(value)
                break
            case 'country':
                setCountry(value)
                break
            case 'releaseDate':
                setReleaseDate(value)
                break
            case 'genre':
                setGenre(value)
                break
            case 'minimalAge':
                setMinimalAge(value)
                break
            case 'imageUrl':
                setImageUrl(value)
                break
            case 'watchTime':
                setWatchTime(value)
                break
            case 'rating':
                setRating(value)
                break
            default:
                return ''
        }
    }

    function fillForm(formRef) {
        formRef.preventDefault()
        const form = new FormData(formRef)
        const json = {
            filmName: form.get('filmName'),
            country: form.get('country'),
            releaseDate: form.get('releaseDate'),
            genre: form.get('genre'),
            minimalAge: form.get('minimalAge'),
            imageUrl: form.get('imageUrl'),
            watchTime: form.get('watchTime'),
            rating: form.get('rating'),
        }
        console.log(json)
    }

    function form() {
        return (
            <form
                onSubmit={fillForm}
                ref={formRef}
                className={
                    'flex w-fit flex-col content-center items-center justify-center gap-3 rounded-2xl bg-[#f4f3ee] p-4'
                }
            >
                <ul className="w-fit">
                    <div
                        id="close-form-sign"
                        className="relative mt-[-10px] flex justify-between p-0"
                    >
                        <div className="w-[95%]"></div>

                        <Link to={'/'}>
                            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer ">
                                X
                            </p>
                        </Link>
                    </div>
                    <li className="mt-[-15px] text-center text-3xl font-bold">
                        add film
                    </li>
                    <li className="form-li">
                        <p>film name</p>
                        <input
                            ref={filmNameRef}
                            className="input"
                            type="search"
                            name="filmName"
                            value={filmName}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('filmName')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>country</p>
                        <input
                            ref={countryRef}
                            className="input"
                            type="search"
                            name="country"
                            value={country}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('country')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>release date</p>
                        <input
                            ref={releaseDateRef}
                            className="input w-full"
                            type="date"
                            name="releaseDate"
                            value={releaseDate}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('releaseDate')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>genre</p>
                        <input
                            ref={genreRef}
                            className="input"
                            type="search"
                            name="genre"
                            value={genre}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('genre')}
                        </div>
                    </li>
                    <li className="form-li" ref={minimalAgeRef}>
                        <p>age</p>
                        <div className="flex w-full justify-evenly">
                            {inputsContent()}
                        </div>
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('minimalAge')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>poster</p>
                        <input
                            ref={imageUrlRef}
                            type="file"
                            className="scale-90"
                            name="imageUrl"
                            value={imageUrl}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('imageUrl')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>video</p>
                        <input
                            type="file"
                            ref={videoUrlRef}
                            className="scale-90"
                            name="videoUrl"
                            value={videoUrl}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('imageUrl')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>watch time</p>
                        <input
                            ref={watchTimeRef}
                            className="input"
                            type="text"
                            placeholder={'hh:mm'}
                            name="watchTime"
                            value={watchTime}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('watchTime')}
                        </div>
                    </li>
                    <li className="form-li">
                        <p>rating</p>
                        <input
                            ref={ratingRef}
                            className="input"
                            type="text"
                            placeholder="6.89"
                            name="rating"
                            value={rating}
                            onChange={handleInputChange}
                        />
                        <div className="relative-structure-to-hold-type-suggestions">
                            {typingSuggestions('rating')}
                        </div>
                    </li>
                </ul>
                <div className="button-aligner">
                    <button
                        className="rounded-2xl bg-red-600 p-1.5 font-bold text-white"
                        id="add-film-button"
                    >
                        submit
                    </button>
                </div>
            </form>
        )
    }

    function inputsContent() {
        return (
            <>
                <div>
                    <input type="radio" name="minimalAge" value="0" />
                    <label htmlFor="" className="ml-0.5">
                        0
                    </label>
                </div>
                <div>
                    <input type="radio" name="minimalAge" value="6" />
                    <label htmlFor="" className="ml-0.5">
                        6
                    </label>
                </div>
                <div>
                    <input type="radio" name="minimalAge" value="12" />
                    <label htmlFor="" className="ml-0.5">
                        12
                    </label>
                </div>
                <div>
                    <input type="radio" name="minimalAge" value="16" />
                    <label htmlFor="" className="ml-0.5">
                        16
                    </label>
                </div>
                <div>
                    <input type="radio" name="minimalAge" value="18" />
                    <label htmlFor="" className="ml-0.5">
                        18
                    </label>
                </div>
            </>
        )
    }

    return (
        <>
            <div className="column flex flex-col justify-center">{form()}</div>
        </>
    )
}

export default FormAddFilm
