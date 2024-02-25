import React, { Children, useEffect, useEjfect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'

function FormAddFilm() {
    // *** ASSIGNMENT
    const serverUrl = 'http://localhost:8080'
    const suggestionsTextHighlightColor = '#f72585'

    const [retrievedSuggestionsDivs, setRetrievedSuggestionsDivs] = useState([])

    // auto filling fields
    const [genreFillerContent, setGenreFillerContent] = useState(null)
    const [countyFillerContent, setCountryFillerContent] = useState(null)
    const contentFillers = [
        genreFillerContent,
        countyFillerContent
    ]

    function fetchAutosuggestionsList(name, inputVal) {
        if (name === 'genre') {
            let url = `${serverUrl}/fillingAssistants/genres/get/bySequence?sequence=`
            url = url.concat(inputVal)
            return fetch(url)
        } else if (name === 'country') {
            let url = `${serverUrl}/fillingAssistants/countries/get/bySequence?sequence=`
            let countryName = ''
            if (inputVal.length > 0) {
                countryName =
                    inputVal[0].toUpperCase() +
                    inputVal.substring(1, inputVal.length)
            }
            url = url.concat(countryName)
            return fetch(url)
        }
        return Promise.resolve(null)
    }

    function createDivFromRetrievedSuggestion(promise) {
        promise
            .then((response) => response.json())
            .then((data) => {
                const listItemsDiv = Object.keys(data).map((k) => (
                    <button
                        className="text-left " // absolute
                        type="submit"
                        onClick={null}
                        key={k}
                    >
                        {data[k]}
                    </button>
                ))
                setRetrievedSuggestionsDivs(listItemsDiv)
            })
            .catch((e) => {
                console.error('Error fetching or parsing data:', e)
            })
    }

    function applyTextAutosuggestion(name, inputVal) {
        const promise = fetchAutosuggestionsList(name, inputVal)
        createDivFromRetrievedSuggestion(promise)

        contentFillers.map(el => el = null)
        if (name === "country") {
            console.log(retrievedSuggestionsDivs)
            setCountryFillerContent(retrievedSuggestionsDivs)
        }
        if (name === "genre") {
            setGenreFillerContent(retrievedSuggestionsDivs)
        }
    }

    // function highlightPopupElementTextColorWhileTyping() {
    //     let length = inputValue.length
    //     const suggestedVariants = focusedPopup.current.children
    //     for (let suggestedVariant of suggestedVariants) {
    //         suggestedVariant.innerHTML =
    //             `<span style="color: ${suggestionsTextHighlightColor};">` +
    //             `${suggestedVariant.textContent.substring(0, length)}</span>` +
    //             `${suggestedVariant.textContent.substring(
    //                 length,
    //                 suggestedVariant.textContent.length
    //             )}`
    //     }
    // }

    function displayOrHideSuggestionsBlock(suggestion) {
        suggestion.addEventListener('focus', () => {
            suggestion.style.backgroundColor = '#2b2d42'
            suggestion.style.color = 'white'
        })
        suggestion.addEventListener('blur', () => {
            suggestion.style.backgroundColor = '' // Reset the background color when focus is removed
            suggestion.style.color = 'black'
        })
        suggestion.addEventListener('click', () => {
            // focusedPopup.current.style.display = 'none'
        })
    }

    function autoCompleteSuggestionOnClick(suggestion) {
        suggestion.addEventListener('click', () => {
            // focusedReference.current.value = suggestion.textContent
        })
    }

    function prepareFormDataToSend(formRef) {
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
                onSubmit={() => prepareFormDataToSend()}
                className="dark:bg-stone-80r flex w-fit flex-col content-center items-center justify-center gap-3 rounded-2xl bg-[#f4f3ee] p-4 dark:text-blue-100"
            >
                <ul className="w-fit">
                    <li
                        id="close-form-sign"
                        className="relative mt-[-10px] flex justify-between p-0"
                    >
                        <div className="w-[95%]"></div>
                        <Link to={'/'}>
                            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer ">
                                X
                            </p>
                        </Link>
                    </li>
                    <li className="mb-2 mt-[-10px] text-center text-3xl font-bold">
                        Добавить фильм
                    </li>
                    <li id="filmName" className="form-li">
                        <p>Название фильма</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="filmName"
                        />
                    </li>
                    <li id="country" className="form-li">
                        <p>Страна</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="country"
                            onChange={(ev) =>
                                applyTextAutosuggestion(
                                    'country',
                                    ev.target.value
                                )
                            }
                        />
                        <div className="typingSuggestions">{countyFillerContent}</div>
                    </li>
                    <li id="releaseDate" className="form-li">
                        <p>Дата релиза</p>
                        <input
                            className="input w-full pl-2"
                            type="date"
                            name="releaseDate"
                        />
                    </li>
                    <li id="genre" className="form-li">
                        <p>Жанр</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="genre"
                            onChange={(ev) =>
                                applyTextAutosuggestion(
                                    'genre',
                                    ev.target.value
                                )
                            }
                        />
                        <div className="typingSuggestions">{genreFillerContent}</div>
                    </li>
                    <li id="ageRestriction" className="form-li">
                        <p>Возраст</p>
                        <div className="flex w-full justify-evenly">
                            {setAgeDiv()}
                        </div>
                    </li>
                    <li id="poster" className="form-li">
                        <p>Постер</p>
                        <input
                            type="file"
                            className="scale-90"
                            name="imageUrl"
                        />
                    </li>
                    <li id="video" className="form-li">
                        <p>Видео</p>
                        <input
                            type="file"
                            className="scale-90"
                            name="videoUrl"
                        />
                    </li>
                    <li className="form-li">
                        <p>Время просмотра</p>
                        <input
                            className="input pl-2"
                            type="text"
                            placeholder={'ч:мм'}
                            name="watchTime"
                        />
                    </li>
                    <li className="form-li">
                        <p>Рейтинг</p>
                        <input
                            className="input pl-2"
                            type="text"
                            placeholder="6.89"
                            name="rating"
                        />
                    </li>
                </ul>
                <div className="button-aligner">
                    <button
                        className="rounded-2xl bg-red-600 p-1.5 font-bold text-white"
                        id="add-film-button"
                    >
                        Принять
                    </button>
                </div>
            </form>
        )
    }

    function setAgeDiv() {
        return (
            <>
                {createAgeDiv(0)}
                {createAgeDiv(6)}
                {createAgeDiv(12)}
                {createAgeDiv(16)}
                {createAgeDiv(18)}
            </>
        )

        function createAgeDiv(age) {
            return (
                <div>
                    <input type="radio" name="minimalAge" value={age} />
                    <label htmlFor="" className="ml-0.5">
                        {age}+
                    </label>
                </div>
            )
        }
    }

    return (
        <>
            <div className="column flex flex-col justify-center">{form()}</div>
        </>
    )
}

export default FormAddFilm
