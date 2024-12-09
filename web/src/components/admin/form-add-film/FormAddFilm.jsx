import React, {useEffect, useRef, useState} from "react";
// TODO use expo router instead
// import { Link } from 'react-router-dom';

export default function FormAddFilm() {
    const FILLING_ASSISTANT_URL = `http://localhost:8001`;
    const STORAGE_URL = "http://localhost:8080";

    const options = {
        MAX_AUTO_SUGGESTIONS_DISPLAYED: 5,
    };

    const [suggestionsDOM, setSuggestionsDOM] = useState([]);

    const [countryInput, setCountryInput] = useState("");
    const [mainGenreInput, setMainGenreInput] = useState("");
    const [subGenresInput, setSubGenresInput] = useState("");

    const [countrySuggestionsDOM, setCountrySuggestionsDOM] = useState(<div/>);
    const [mainGenreSuggestionsDOM, setMainGenreSuggestionsDOM] = useState(
        <div/>,
    );
    const [subGenresSuggestionsDOM, setSubGenresSuggestionsDOM] = useState(
        <div/>,
    );

    const posterInputRef = useRef();
    const videoInputRef = useRef();

    const [autoSuggestionsMap, setAutoSuggestionsMap] = useState({});

    // *** AUTO SUGGESTIONS

    useEffect(() => {
        // fetchAutosuggestions();

        async function fetchAutosuggestions() {
            let map = {
                buffer: [],
                recentInputLength: 0,
            };
            let response = await fetch(
                `${FILLING_ASSISTANT_URL}/filling-assistants/genres/get/all`,
            ).catch(() => {
                console.error(
                    `problem fetching: ${FILLING_ASSISTANT_URL}/filling-assistants/genres/get/all`,
                );
                return;
            });

            let responseData = await response.json();
            map = {
                ...map,
                genre: responseData,
            };

            response = await fetch(
                `${FILLING_ASSISTANT_URL}/filling-assistants/countries/get/all`,
            ).catch(() => {
                console.error(
                    `problem fetching: ${FILLING_ASSISTANT_URL}/filling-assistants/countries/get/all`,
                );
            });
            responseData = await response.json();
            map = {
                ...map,
                country: responseData,
            };

            setAutoSuggestionsMap(map);
        }
    }, []);

    function createDivFromRetrievedSuggestion(
        suggestions,
        formFieldName,
        inputValue,
    ) {
        return suggestions.map((suggestion) => createDOM(suggestion));

        function createDOM(suggestion) {
            return (
                <button
                    className={`bg-white px-2 text-left first:rounded-t last:rounded-b dark:bg-slate-900 dark:text-white dark:focus:bg-slate-700 dark:focus:text-teal-300`}
                    type="submit"
                    onClick={(ev) => {
                        ev.preventDefault();
                        const autoSuggestion = ev.currentTarget.textContent;
                        changeInputTextToAutoSuggestion(autoSuggestion);
                        setAutoSuggestionsMap({
                            ...autoSuggestionsMap,
                            buffer: [],
                            recentInputLength: 0,
                        });
                    }}
                    key={suggestion}
                >
                    {highlightSuggestionMatchedLetters(suggestion)}
                </button>
            );
        }

        function highlightSuggestionMatchedLetters(suggestion) {
            return (
                <>
          <span className="font-bold">
            {suggestion.substring(0, inputValue.length)}
          </span>
                    {suggestion.substring(inputValue.length, suggestion.length)}
                </>
            );
        }

        function changeInputTextToAutoSuggestion(autoSuggestion) {
            if (formFieldName === "country") setCountryInput(autoSuggestion);
            if (formFieldName === "genre") setMainGenreInput(autoSuggestion);
        }
    }

    function getSuggestionsBySequence(input, list) {
        list = list.filter((string) => string.substring(0, input.length) === input);
        return list.slice(0, options.MAX_AUTO_SUGGESTIONS_DISPLAYED);
    }

    // Set country suggestions
    useEffect(() => {
        if (countryInput === undefined || countryInput === "") {
            return;
        }

        const countries = autoSuggestionsMap.country;
        const buffer = autoSuggestionsMap.buffer;
        const recentInputLength = autoSuggestionsMap.recentInputLength;
        const firstCharUpCaseInput =
            countryInput[0].toUpperCase() +
            countryInput.substring(1, countryInput.length);

        let list = countryInput.length === 1 ? countries : buffer;

        if (countryInput.length < recentInputLength) {
            list = countries;
        }

        list = getSuggestionsBySequence(firstCharUpCaseInput, list);

        setAutoSuggestionsMap({
            ...autoSuggestionsMap,
            buffer: list,
            recentInputLength: countryInput.length,
        });

        const DOM = createDivFromRetrievedSuggestion(list, "country", countryInput);
        setCountrySuggestionsDOM(DOM);
    }, [countryInput]);

    // Set genre suggestions
    useEffect(() => {
        if (mainGenreInput === undefined || mainGenreInput === "") {
            return;
        }

        const genres = autoSuggestionsMap.genre;
        const buffer = autoSuggestionsMap.buffer;
        const recentInputLength = autoSuggestionsMap.recentInputLength;

        let list = mainGenreInput.length === 1 ? genres : buffer;

        if (mainGenreInput.length < recentInputLength) {
            list = genres;
        }

        list = getSuggestionsBySequence(mainGenreInput, list);

        setAutoSuggestionsMap({
            ...autoSuggestionsMap,
            buffer: list,
            recentInputLength: countryInput.length,
        });

        const DOM = createDivFromRetrievedSuggestion(list, "genre", mainGenreInput);
        setMainGenreSuggestionsDOM(DOM);
    }, [mainGenreInput]);

    // *** FORM

    const formRef = useRef();
    const formSaveStatus = useRef();
    const loadingRef = useRef();

    async function prepareFormDataToSend() {
        async function savePoster() {
            return new Promise((resolve) => {
                const posterFile = posterInputRef.current.files[0];
                if (posterFile == null) {
                    return;
                }
                const posterFormData = new FormData();
                posterFormData.append("file", posterFile);
                fetch(`${STORAGE_URL}/posters/upload`, {
                    method: "POST",
                    body: posterFormData,
                })
                    .then((res) => res.text())
                    .then((id) => {
                        return resolve(id);
                    });
            });
        }

        async function saveVideo() {
            return new Promise((resolve) => {
                const videoFile = videoInputRef.current.files[0];
                if (videoFile == null) {
                    return;
                }
                const videoFormData = new FormData();
                videoFormData.append("file", videoFile);
                fetch(`${STORAGE_URL}/videos/upload`, {
                    method: "POST",
                    body: videoFormData,
                })
                    .then((res) => res.text())
                    .then((id) => {
                        return resolve(id);
                    });
            });
        }

        async function saveMetadata(posterId, videoId) {
            return new Promise((resolve) => {
                const form = new FormData(formRef.current);
                const metadata = {
                    title: form.get("title").trim(),
                    releaseDate: form.get("releaseDate"),
                    country: form.get("country").trim(),
                    mainGenre: form.get("mainGenre").trim(),
                    subGenres: parseSubGenres(form.get("subGenres")),
                    age: form.get("age").trim(),
                    rating: form.get("rating").trim(),
                    posterId: posterId,
                    videoId: videoId,
                };

                // TODO make additional checks for input
                function parseSubGenres(subGenresString) {
                    const splitted = subGenresString.split(",");
                    let subGenresArray = [];
                    for (const string of splitted) {
                        if (string.length !== 0) subGenresArray.push(string.trim());
                    }
                    return subGenresArray;
                }

                fetch(`${STORAGE_URL}/videos/metadata/save`, {
                    method: "POST",
                    headers: {
                        "Content-type": "application/json",
                    },
                    body: JSON.stringify(metadata),
                })
                    .then((res) => res.text())
                    .then((id) => resolve(id));
            });
        }

        let posterId = await savePoster();
        const videoId = await saveVideo();
        const metadataId = await saveMetadata(posterId, videoId);
        const statusBar = formSaveStatus.current;

        loadingRef.current.style.display = "none";
        // posterId = undefined
        if (
            posterId === undefined ||
            posterId === "" ||
            videoId === undefined ||
            videoId === "" ||
            metadataId === undefined ||
            metadataId === ""
        ) {
            // TODO make option to read log if something gone wrong was not uploaded
            statusBar.innerHTML = "Ошибка при сохранении. <u>Подробнее</u>";
            statusBar.style.color = "red";
            return;
        }

        displaySuccessSaveMessage();

        function displaySuccessSaveMessage() {
            statusBar.textContent = "Сохранено ✅";
            statusBar.style.fontSize = "0.8em";
            statusBar.style.color = "green";
            statusBar.style.marginTop = "-6px";
            setTimeout(() => {
                statusBar.textContent = "";
                statusBar.style.fontSize = "0.7em";
            }, 1500);
        }
    }

    function createAgeRadioInput(age) {
        return (
            <>
                <input
                    onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                    type="radio"
                    name="age"
                    value={age}
                />
                <label htmlFor="" className="ml-0.5">
                    {age}+
                </label>
            </>
        );
    }

    function handleSubmitButton(event) {
        event.preventDefault();
        prepareFormDataToSend();
        animateButtonPress();
        showLoadingIcon();

        function animateButtonPress() {
            const el = event.currentTarget;
            el.style.transform = "scale(0.95)";
            el.classList.add("bg-green-600");
            setTimeout(() => {
                el.style.transform = "scale(1)";
                el.classList.remove("bg-green-600");
            }, 230);
        }

        function showLoadingIcon() {
            loadingRef.current.style.display = "block";
        }
    }

    return (
        <div className="flex min-h-screen w-full items-center justify-center bg-gray-100 dark:bg-gray-900 p-4">
            <form
                ref={formRef}
                className="relative w-full max-w-md rounded-lg bg-white p-6 shadow-lg dark:bg-neutral-800 dark:text-blue-100"
            >
                {/* Close Button */}
                <div className="absolute top-4 right-4">
                    <p
                        id="close-form-sign"
                        className="cursor-pointer select-none text-xl font-bold text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300"
                    >
                        X
                    </p>
                </div>

                <h2 className="mb-6 text-center text-3xl font-bold text-gray-900 dark:text-blue-100">
                    Добавить фильм
                </h2>

                <ul className="space-y-4">
                    <li id="title">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Название фильма
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            onSubmit={(event) => event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="search"
                            name="title"
                        />
                    </li>

                    <li id="country">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Страна
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="search"
                            name="country"
                            value={countryInput}
                            onChange={(ev) => setCountryInput(ev.target.value)}
                        />
                        <div className="relative mt-1">
                            <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700">
                                {countrySuggestionsDOM}
                            </div>
                        </div>
                    </li>

                    <li id="releaseDate">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Дата релиза
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="date"
                            name="releaseDate"
                        />
                    </li>

                    <li id="mainGenre">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Основной жанр
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="search"
                            name="mainGenre"
                            value={mainGenreInput}
                            onChange={(ev) => setMainGenreInput(ev.target.value)}
                        />
                        <div className="relative mt-1">
                            <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700">
                                {mainGenreSuggestionsDOM}
                            </div>
                        </div>
                    </li>

                    <li id="subGenres">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Поджанры
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="search"
                            name="subGenres"
                            placeholder="разделять запятой"
                            onChange={() => {
                            }}
                        />
                        <div className="relative mt-1">
                            <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700">
                                {subGenresSuggestionsDOM}
                            </div>
                        </div>
                    </li>

                    <li id="ageRestriction">
                        <p className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Возраст
                        </p>
                        <div className="flex w-full justify-between gap-2">
                            {createAgeRadioInput(0)}
                            {createAgeRadioInput(6)}
                            {createAgeRadioInput(12)}
                            {createAgeRadioInput(16)}
                            {createAgeRadioInput(18)}
                        </div>
                    </li>

                    <li id="poster">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Постер
                        </label>
                        <input
                            ref={posterInputRef}
                            type="file"
                            className="block w-full text-sm text-gray-700 dark:text-blue-100 file:mr-4 file:rounded-md file:border-0 file:bg-blue-50 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100"
                            name="imageUrl"
                        />
                    </li>

                    <li id="video">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Видео
                        </label>
                        <input
                            ref={videoInputRef}
                            type="file"
                            className="block w-full text-sm text-gray-700 dark:text-blue-100 file:mr-4 file:rounded-md file:border-0 file:bg-blue-50 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100"
                            name="videoUrl"
                        />
                    </li>

                    <li>
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Рейтинг
                        </label>
                        <input
                            onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="text"
                            placeholder="6.89"
                            name="rating"
                        />
                    </li>
                </ul>

                <div className="relative mt-6 flex items-center justify-center">
                    {/* Status & Loading */}
                    <div className="absolute left-0 flex h-12 w-24 items-center justify-center overflow-hidden">
                        <p
                            className="m-0 p-0 text-center text-xs font-medium text-gray-700 dark:text-blue-100"
                            ref={formSaveStatus}
                        ></p>
                        <img
                            ref={loadingRef}
                            src="icons/loading.gif"
                            className="hidden w-7"
                        />
                    </div>
                    {/* Submit Button */}
                    <button
                        onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
                        className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white transition-transform hover:bg-blue-700 focus:outline-none dark:bg-green-600 dark:hover:bg-green-700"
                        id="add-film-button"
                        onClick={handleSubmitButton}
                    >
                        Принять
                    </button>
                    {/* Clear Form */}
                    <div className="absolute right-0">
                        <button
                            className="m-0 p-0 text-xs text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300">
                            <u>Очистить форму</u>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    );

}