import React, { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'

function FormAddFilm() {
    // *** ASSIGNMENT
    const FILLING_ASSISTANT_URL = 'http://localhost:8001'
    const BINARY_STORAGE_URL = 'http://localhost:8080'
    const suggestionsTextHighlightColor = '#f72585'

    const [retrievedSuggestionsDivs, setRetrievedSuggestionsDivs] = useState([])

    const [currentInputName, setCurrentInputName] = useState('')
    const [currentInputValue, setCurrentInputValue] = useState('')
    // auto filling
    const [countryInput, setCountryInput] = useState('')
    const [genreInput, setGenreInput] = useState('')
    const [genreFillerContent, setGenreFillerContent] = useState(null)
    const [countryFillerContent, setCountryFillerContent] = useState(null)
    const autoSuggestionDivs = [genreFillerContent, countryFillerContent]

    const posterInputRef = useRef()
    const videoInputRef = useRef()

    async function fetchAutosuggestionsList(name, inputVal) {
        if (name === 'genre') {
            let url = `${FILLING_ASSISTANT_URL}/fillingAssistants/genres/get/bySequence?sequence=`
            url = url.concat(inputVal)
            return fetch(url)
        } else if (name === 'country') {
            let countryName = ''
            let url = `${FILLING_ASSISTANT_URL}/fillingAssistants/countries/get/bySequence?sequence=`
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

    function highlightPopupElementTextColorWhileTyping(input) {}

    function createDivFromRetrievedSuggestion(promise, name, inputVal) {
        promise
            .then((response) => response.json())
            .then((data) => {
                const listItemsDiv = Object.keys(data).map((k) => (
                    <button
                        className="@/h.h// bg-amber-100 px-2 text-left dark:bg-slate-900 dark:text-white dark:focus:bg-slate-700   dark:focus:text-teal-300"
                        type="submit"
                        onClick={(ev) => {
                            ev.preventDefault()
                            const autoSuggestion = ev.currentTarget.textContent
                            if (
                                autoSuggestion === inputVal &&
                                inputVal.length !== 0
                            ) {
                                ev.currentTarget.style.display = 'none'
                            }
                            changeInputTextToAutoSuggestion(
                                autoSuggestion,
                                name
                            )
                            setRetrievedSuggestionsDivs([])
                        }}
                        onFocus={(ev) => {
                            whenLastElementFocused_hideSuggestionsOnBlur(
                                ev,
                                data,
                                k
                            )
                        }}
                        key={k}
                    >
                        {data[k]}
                    </button>
                ))
                setRetrievedSuggestionsDivs(listItemsDiv)
            })

        function changeInputTextToAutoSuggestion(autoSuggestion, name) {
            if (name === 'country') {
                setCountryInput(autoSuggestion)
            }
            if (name === 'genre') {
                setGenreInput(autoSuggestion)
            }
        }

        function whenLastElementFocused_hideSuggestionsOnBlur(ev, data, k) {
            if (parseInt(k) === data.length - 1) {
                ev.currentTarget.addEventListener('blur', () => {
                    setRetrievedSuggestionsDivs([])
                })
            }
        }
    }

    useEffect(() => {
        setCurrentInputName('country')
        setCurrentInputValue(countryInput)
        applyTextAutosuggestion('country', countryInput)
    }, [countryInput])

    useEffect(() => {
        setCurrentInputName('genre')
        setCurrentInputValue(genreInput)
        applyTextAutosuggestion('genre', genreInput)
    }, [genreInput])

    function applyTextAutosuggestion(name, inputVal) {
        const promise = fetchAutosuggestionsList(name, inputVal)
        createDivFromRetrievedSuggestion(promise, name, inputVal)
    }

    useEffect(() => {
        if (
            currentInputValue.length !== 0 &&
            retrievedSuggestionsDivs.length !== 0
        ) {
            if (
                currentInputValue.length ===
                retrievedSuggestionsDivs[0].props.children.length
            ) {
                setRetrievedSuggestionsDivs([])
            }
        }
        if (currentInputName === 'country') {
            setCountryFillerContent(retrievedSuggestionsDivs)
        }
        if (currentInputName === 'genre') {
            setGenreFillerContent(retrievedSuggestionsDivs)
        }
    }, [retrievedSuggestionsDivs])

    const formRef = useRef()

    async function prepareFormDataToSend() {
        async function savePoster() {
            return new Promise(resolve => {
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
            return new Promise(resolve => {
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

        async function saveMetadata(posterId, videoId) {
            return new Promise(resolve => {
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

        const posterId = await savePoster()
        const videoId = await saveVideo()
        const metadataId = await saveMetadata(posterId, videoId)
        console.log(metadataId)
    }

    function form() {
        return (
            <form
                ref={formRef}
                // onSubmit={(event) => {
                //     prepareFormDataToSend(event.currentTarget);
                // }}
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
                            {countryFillerContent}
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
                            {genreFillerContent}
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
                <div className="button-aligner">
                    <button
                        onKeyDown={(event) =>
                            event.keyCode === 13 && event.preventDefault()
                        }
                        className="rounded-2xl bg-red-600 p-1.5 font-bold text-white"
                        id="add-film-button"
                        onClick={(ev) => {
                            ev.preventDefault()
                            prepareFormDataToSend()
                        }}
                    >
                        Принять
                    </button>
                </div>
            </form>
        )
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

    return <div className="column flex flex-col justify-center">{form()}</div>
}

export default FormAddFilm
