import React, { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'

function FormAddFilm() {
    // *** ASSIGNMENT
    const FILLING_ASSISTANT_URL = `${process.env.REACT_APP_SERVER_URL}:8001`
    const BINARY_STORAGE_URL = `${process.env.REACT_APP_SERVER_URL}:8080`

    const options = {
        MAX_AUTO_SUGGESTIONS_DISPLAYED: 5,
    }

    const [suggestionsDOM, setSuggestionsDOM] = useState([])

    const [countryInput, setCountryInput] = useState('')
    const [genreInput, setGenreInput] = useState('')
    const [genreSuggestionsDOM, setGenreSuggestionsDOM] = useState(null)
    const [countrySuggestionsDOM, setCountrySuggestionsDOM] = useState(<div />)

    const posterInputRef = useRef()
    const videoInputRef = useRef()

    const [autoSuggestionsMap, setAutoSuggestionsMap] = useState({})
    const [autoSuggestionsBuffer, setAutoSuggestionsBuffer] = useState([])

    // *** AUTO SUGGESTIONS

    useEffect(() => {
        fetchAutosuggestions()

        async function fetchAutosuggestions() {
            let response = await fetch(
                `${FILLING_ASSISTANT_URL}/filling-assistants/genres/get/all`
            )
            let responseData = await response.json()
            let map = {
                genre: responseData,
            }

            response = await fetch(
                `${FILLING_ASSISTANT_URL}/filling-assistants/countries/get/all`
            )
            responseData = await response.json()
            map = {
                ...map,
                country: responseData,
            }

            setAutoSuggestionsMap(map)
        }
    }, [])

    function createDivFromRetrievedSuggestion(
        suggestions,
        formFieldName,
        inputVal
    ) {
        return suggestions.map((suggestion) => createDOM(suggestion))

        function createDOM(suggestion, v) {
            return (
                <button
                    className="bg-amber-100 px-2 text-left dark:bg-slate-900 dark:text-white dark:focus:bg-slate-700 dark:focus:text-teal-300"
                    type="submit"
                    onClick={(ev) => {
                        ev.preventDefault()
                        const autoSuggestion = ev.currentTarget.textContent
                        // if (
                        //     autoSuggestion === inputVal &&
                        //     inputVal.length !== 0
                        // ) {
                        //     // console.log("POP@!")
                        //     destroySuggestionsPopup(formFieldName)
                        // }
                        // destroySuggestionsPopup(formFieldName)
                        changeInputTextToAutoSuggestion(
                            autoSuggestion,
                            formFieldName
                        )
                        // destroySuggestionsPopup(formFieldName)
                        // setSuggestionsDOM([])
                    }}
                    onFocus={(ev) => {
                        ev.currentTarget.addEventListener('blur', () => {
                            //     setSuggestionsDOM([])
                        })
                    }}
                    key={suggestion}
                >
                    {suggestion}
                </button>
            )
        }

        function changeInputTextToAutoSuggestion(
            autoSuggestion,
            formFieldName
        ) {
            if (formFieldName === 'country') setCountryInput(autoSuggestion)
            if (formFieldName === 'genre') setGenreInput(autoSuggestion)
        }

        // function destroySuggestionsPopup(formFieldName) {
        //     if (formFieldName === 'country') setCountrySuggestionsDOM(<div />)
        //     if (formFieldName === 'genre') setGenreSuggestionsDOM(<div />)
        // }
    }

    // TODO make narrowing list each time value found
    useEffect(() => {
        if (countryInput === undefined || countryInput === '') {
            return
        }
        setGenreSuggestionsDOM('')
        let list = autoSuggestionsMap.country
        const firstCharUpCaseInput =
            countryInput[0].toUpperCase() +
            countryInput.substring(1, countryInput.length)
        list = list.filter(
            (countryName) =>
                countryName.substring(0, countryInput.length) ===
                firstCharUpCaseInput
        )
        list = list.slice(0, options.MAX_AUTO_SUGGESTIONS_DISPLAYED)
        const DOM = createDivFromRetrievedSuggestion(
            list,
            'country',
            countryInput
        )
        setCountrySuggestionsDOM(DOM)
    }, [countryInput])

    useEffect(() => {
        if (genreInput === undefined || genreInput === '') {
            return
        }
        setCountrySuggestionsDOM('')
        let list = autoSuggestionsMap.genre
        list = list.filter(
            (genreName) =>
                genreName.substring(0, genreInput.length) === genreInput
        )
        list = list.slice(0, options.MAX_AUTO_SUGGESTIONS_DISPLAYED)
        const DOM = createDivFromRetrievedSuggestion(list, 'genre', genreInput)
        setGenreSuggestionsDOM(DOM)
    }, [genreInput])

    function destroySuggestionsPopup(formFieldName) {
        if (formFieldName === 'country') setCountrySuggestionsDOM(<div />)
        if (formFieldName === 'genre') setGenreSuggestionsDOM(<div />)
    }

    // *** FORM

    const formRef = useRef()
    const formSaveStatus = useRef()
    const loadingRef = useRef()

    async function prepareFormDataToSend() {
        async function savePoster() {
            return new Promise((resolve) => {
                const posterFile = posterInputRef.current.files[0]
                if (posterFile == null) {
                    return
                }
                const posterFormData = new FormData()
                posterFormData.append('file', posterFile)
                fetch(`${BINARY_STORAGE_URL}/posters/upload`, {
                    method: 'POST',
                    body: posterFormData,
                })
                    .then((res) => res.text())
                    .then((id) => {
                        return resolve(id)
                    })
            })
        }

        async function saveVideo() {
            return new Promise((resolve) => {
                const videoFile = videoInputRef.current.files[0]
                if (videoFile == null) {
                    return
                }
                const videoFormData = new FormData()
                videoFormData.append('file', videoFile)
                fetch(`${BINARY_STORAGE_URL}/videos/upload`, {
                    method: 'POST',
                    body: videoFormData,
                })
                    .then((res) => res.text())
                    .then((id) => {
                        return resolve(id)
                    })
            })
        }

        function highlightPopupElementTextColorWhileTyping(input) {}

        async function saveMetadata(posterId, videoId) {
            return new Promise((resolve) => {
                const form = new FormData(formRef.current)
                const metadata = {
                    title: form.get('title'),
                    releaseDate: form.get('releaseDate'),
                    country: form.get('country'),
                    genre: form.get('genre'),
                    age: form.get('age'),
                    rating: form.get('rating'),
                    posterId: posterId,
                    videoId: videoId,
                }
                fetch(`${BINARY_STORAGE_URL}/videos/save/metadata`, {
                    method: 'POST',
                    headers: {
                        'Content-type': 'application/json',
                    },
                    body: JSON.stringify(metadata),
                })
                    .then((res) => res.text())
                    .then((id) => {
                        resolve(id)
                    })
            })
        }

        let posterId = await savePoster()
        const videoId = await saveVideo()
        const metadataId = await saveMetadata(posterId, videoId)
        const statusBar = formSaveStatus.current

        loadingRef.current.style.display = 'none'
        // posterId = undefined
        if (
            posterId === undefined ||
            posterId === '' ||
            videoId === undefined ||
            videoId === '' ||
            metadataId === undefined ||
            metadataId === ''
        ) {
            // TODO make option to read log if something gone wrong was not uploaded
            statusBar.innerHTML = 'Ошибка при сохранении. <u>Подробнее</u>'
            statusBar.style.color = 'red'
            return
        }

        displaySuccessSaveMessage()

        function displaySuccessSaveMessage() {
            statusBar.textContent = 'Сохранено ✅'
            statusBar.style.fontSize = '0.8em'
            statusBar.style.color = 'green'
            statusBar.style.marginTop = '-6px'
            setTimeout(() => {
                statusBar.textContent = ''
                statusBar.style.fontSize = '0.7em'
            }, 1500)
        }
    }

    function createAgeRadioInput(age) {
        return (
            <>
                <input
                    onKeyDown={(event) =>
                        event.keyCode === 13 && event.preventDefault()
                    }
                    type="radio"
                    name="age"
                    value={age}
                />
                <label htmlFor="" className="ml-0.5">
                    {age}+
                </label>
            </>
        )
    }

    return (
        <div className="column flex flex-col justify-center">
            <form
                ref={formRef}
                className="dark:bg-stone-80r flex w-fit flex-col content-center items-center justify-center gap-3 rounded-2xl bg-[#f4f3ee] p-4 dark:bg-neutral-800 dark:text-blue-100"
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
                    <li id="title" className="form-li">
                        <p>Название фильма</p>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            onSubmit={(event) => event.preventDefault()}
                            className="input pl-2"
                            type="search"
                            name="title"
                        />
                    </li>
                    <li id="country" className="form-li">
                        <p>Страна</p>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            className="input pl-2"
                            type="search"
                            name="country"
                            value={countryInput}
                            onChange={(ev) => setCountryInput(ev.target.value)}
                        />
                        <div className="typingSuggestions">
                            {countrySuggestionsDOM}
                        </div>
                    </li>
                    <li id="releaseDate" className="form-li">
                        <p>Дата релиза</p>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            className="input w-full pl-2"
                            type="date"
                            name="releaseDate"
                        />
                    </li>
                    <li id="genre" className="form-li">
                        <p>Жанр</p>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            className="input pl-2"
                            type="search"
                            name="genre"
                            value={genreInput}
                            onChange={(ev) => setGenreInput(ev.target.value)}
                        />
                        <div className="typingSuggestions">
                            {genreSuggestionsDOM}
                        </div>
                    </li>
                    <li id="ageRestriction" className="form-li">
                        <p>Возраст</p>
                        <div className="flex w-full justify-evenly">
                            {createAgeRadioInput(0)}
                            {createAgeRadioInput(6)}
                            {createAgeRadioInput(12)}
                            {createAgeRadioInput(16)}
                            {createAgeRadioInput(18)}
                        </div>
                    </li>
                    <li id="poster" className="form-li">
                        <p>Постер</p>
                        <input
                            ref={posterInputRef}
                            type="file"
                            className="scale-90"
                            name="imageUrl"
                        />
                    </li>
                    <li id="video" className="form-li">
                        <p>Видео</p>
                        <input
                            ref={videoInputRef}
                            type="file"
                            className="scale-90"
                            name="videoUrl"
                        />
                    </li>
                    <li className="form-li">
                        <p>Рейтинг</p>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            className="input pl-2"
                            type="text"
                            placeholder="6.89"
                            name="rating"
                        />
                    </li>
                </ul>
                <div className="button-aligner flex">
                    <div
                        className={
                            'absolute -ml-28 -mt-1.5 flex h-12 w-24 items-center justify-center overflow-hidden font-medium'
                        }
                    >
                        <p
                            className="m-0 p-0 text-center text-[0.7em]"
                            ref={formSaveStatus}
                        ></p>
                        <img
                            ref={loadingRef}
                            src="icons/loading.gif"
                            className="hidden w-7"
                        />
                    </div>
                    <button
                        onKeyDown={(event) =>
                            event.keyCode === 13 && event.preventDefault()
                        }
                        className="rounded-2xl bg-red-600 p-1.5 font-bold text-white transition-transform"
                        id="add-film-button"
                        onClick={(event) => {
                            event.preventDefault()
                            prepareFormDataToSend()
                            animateButtonPress()
                            showLoadingIcon()

                            function animateButtonPress() {
                                const el = event.currentTarget
                                el.style.transform = 'scale(0.95)'
                                el.classList.add('bg-green-600')
                                setTimeout(() => {
                                    el.style.transform = 'scale(1)'
                                    el.classList.remove('bg-green-600')
                                }, 230)
                            }

                            function showLoadingIcon() {
                                loadingRef.current.style.display = 'block'
                            }
                        }}
                    >
                        Принять
                    </button>
                    <div className="absolute ml-24 mt-1 items-center justify-center">
                        <button className="m-0 p-0 text-[0.70em] opacity-50">
                            <u>Очистить форму</u>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default FormAddFilm
