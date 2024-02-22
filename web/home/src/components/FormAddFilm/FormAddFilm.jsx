import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'



function FormAddFilm() {
    // *** ASSIGNMENT
    const serverUrl = 'http://localhost:8080'
    const suggestionsTextHighlightColor = '#f72585'

    const [focusedReference, setFocusedReference] = useState(null)

    const [focusedPopup, setFocusedPopup] = useState(null)
    // requests data
    const [retrievedSuggestions, setRetrievedSuggestions] = useState([])
    const [inputName, setInputName] = useState(null)
    const [inputValue, setInputValue] = useState(null)

    const formUl = useRef()

    const formFieldsMap = {}
    const autoFillingFormFields = []
    useEffect(() => {
        if (formUl.current.chidren == null) { return }
        for (let li of formUl.current.chidren) {
            formFieldsMap[li.id] = li
        }
        initAutoFillingFormFields()

        function initAutoFillingFormFields() {
            for (const k in formFieldsMap) {
                if (formFieldsMap.hasOwnProperty(k)) {
                    const li = formFieldsMap[k]

                    if (li.children[2] != null && li.children[2].classList.contains("typingSuggestions")) {
                        autoFillingFormFields.push(li)
                    }
                }
            }
        }
    }, [])

    function applyTextAutosuggestion(name) {
        if (autoFillingFormFields == null) return;
        autoFillingFormFields.forEach(li => li.children[2].innerHTML = "");
        const data = fetchAutosuggestionsList(name);
        createDivFromRetrievedSuggestion(data);
    }

    function createDivFromRetrievedSuggestion(promise) {
        promise
            .then((response) => response.json())
            .then((data) => {
                const listItems = Object.keys(data).map((k) => (
                    <button
                        className="text-left " // absolute
                        type="submit"
                        onClick={null}
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

    function fetchAutosuggestionsList(name, inputValue) {
        if (name === 'genre') {
            let url = `${serverUrl}/fillingAssistants/genres/get/bySequence?sequence=`
            url = url.concat(inputValue)
            return fetch(url)
        } else if (name === 'country') {
            let url = `${serverUrl}/fillingAssistants/countries/get/bySequence?sequence=`
            let countryName = ''
            if (inputValue.length > 0) {
                countryName =
                    inputValue[0].toUpperCase() + inputValue.substring(1, inputValue.length)
            }
            url = url.concat(countryName)
            return fetch(url)
        }
        return Promise.resolve(null)
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
    //
    // function hideTypeSuggestionsPopupWhenNotFocused() {
    //     for (let child of inputFieldReferencesList) {
    //         child.current.addEventListener('focus', () => {
    //             focusedPopup.current.style.display = 'none'
    //         })
    //     }
    // }
    //
    // function displayOrHideSuggestionsBlock(suggestion) {
    //     suggestion.addEventListener('focus', () => {
    //         suggestion.style.backgroundColor = '#2b2d42'
    //         suggestion.style.color = 'white'
    //     })
    //     suggestion.addEventListener('blur', () => {
    //         suggestion.style.backgroundColor = '' // Reset the background color when focus is removed
    //         suggestion.style.color = 'black'
    //     })
    //     suggestion.addEventListener('click', () => {
    //         focusedPopup.current.style.display = 'none'
    //     })
    // }
    //
    // function autoCompleteSuggestionOnClick(suggestion) {
    //     suggestion.addEventListener('click', () => {
    //         focusedReference.current.value = suggestion.textContent
    //     })
    // }

    // popups change
    // useEffect(() => {
    //     if (focusedPopup == null || retrievedSuggestions == null) {
    //         return
    //     }
    //     focusedPopup.current.style.display = 'flex'
    //     for (let suggestion of focusedPopup.current.children) {
    //         displayOrHideSuggestionsBlock(suggestion)
    //         autoCompleteSuggestionOnClick(suggestion)
    //     }
    //     highlightPopupElementTextColorWhileTyping()
    //     hideTypeSuggestionsPopupWhenNotFocused()
    // }, [retrievedSuggestions])

    function typingSuggestionsWrapper() {
        return (
            <ul className="flex flex-col bg-sky-200 rounded-ee-2xl">
                {retrievedSuggestions}
            </ul>
        )
    }
    //
    // function launchActionsListForInput(input) {
    //     const { name, value } = input.target
    //     setInputName(name)
    //     setInputValue(value)
    //     const promisedData = retrieveMatches(name, value)
    //     fillContentAssistList(promisedData)
    // }


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
                // onSubmit={prepareFormDataToSend}
                className='flex w-fit flex-col content-center items-center justify-center gap-3 rounded-2xl bg-[#f4f3ee] dark:bg-stone-80r dark:text-blue-100 p-4'
            >
                <ul className="w-fit" ref={formUl}>
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
                    <li id='filmName' className="form-li">
                        <p>Название фильма</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="filmName"
                        />
                    </li>
                    <li id='country' className="form-li">
                        <p>Страна</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="country"
                            onInput={() => applyTextAutosuggestion("country")}
                        />
                        <div className='typingSuggestions'>
                            {typingSuggestionsWrapper('country')}
                        </div>
                    </li>
                    <li id='releaseDate' className="form-li">
                        <p>Дата релиза</p>
                        <input
                            className="input w-full pl-2"
                            type="date"
                            name="releaseDate"
                        />
                    </li>
                    <li id='genre' className="form-li">
                        <p>Жанр</p>
                        <input
                            className="input pl-2"
                            type="search"
                            name="genre"
                            onChange={() => applyTextAutosuggestion("genre")}
                        />
                        <div className='typingSuggestions'>
                            {typingSuggestionsWrapper('genre')}
                        </div>
                    </li>
                    <li id='ageRestriction' className="form-li" >
                        <p>Возраст</p>
                        <div className="flex w-full justify-evenly">
                            {setAgeDiv()}
                        </div>
                    </li>
                    <li id='poster' className="form-li">
                        <p>Постер</p>
                        <input
                            type="file"
                            className="scale-90"
                            name="imageUrl"
                        />
                    </li>
                    <li id='video' className="form-li">
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
            return <div>
                < input type="radio" name="minimalAge" value={age} />
                <label htmlFor="" className="ml-0.5">
                    {age}+
                </label>
            </div >
        }
    }
    return (
        <>
            <div className="column flex flex-col justify-center">{form()}</div>
        </>
    )
}

export default FormAddFilm
