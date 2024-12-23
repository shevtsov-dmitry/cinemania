import React, { useEffect, useRef, useState } from "react";
import useStore from "@/src/state/useStore";
import Constants from "@/src/constants/Constants";
// TODO use expo router instead
// import { Link } from 'react-router-dom';

export default function FormAddFilm() {
  const FILLING_ASSISTANT_URL = Constants.FILLING_ASSISTANT_URL;
  const STORAGE_URL = Constants.STORAGE_URL;

  const options = {
    MAX_AUTO_SUGGESTIONS_DISPLAYED: 5,
  };
  const isFormAddFilmVisible = useStore((state) => state.isFormAddFilmVisible);
  const closeForm = useStore((state) => state.hideFormAddFilm);

  const [suggestionsDOM, setSuggestionsDOM] = useState([]);
  const [countryInput, setCountryInput] = useState("");
  const [mainGenreInput, setMainGenreInput] = useState("");
  const [subGenresInput, setSubGenresInput] = useState("");
  const [countrySuggestionsDOM, setCountrySuggestionsDOM] = useState(<div />);
  const [mainGenreSuggestionsDOM, setMainGenreSuggestionsDOM] = useState(
    <div />,
  );
  const [autoSuggestionsMap, setAutoSuggestionsMap] = useState({});
  const [subGenresSuggestionsDOM, setSubGenresSuggestionsDOM] = useState(
    <div />,
  );

  const posterInputRef = useRef();
  const videoInputRef = useRef();

  // *** AUTO SUGGESTIONS
  // TODO restore autosuggestions functionallity and refactor it into different class. It is better to inject it here with composition.
  useEffect(() => {
    console.log(FILLING_ASSISTANT_URL);
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
  });

  /**
   *
   * @param suggestions {string[]}
   * @param formFieldName {string}
   * @param inputValue {string}
   * @returns {Element} <button>
   */
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

  const [recentFormErrorMessage, setRecentFormErrorMessage] = useState("");
  const [isInfoSignActive, setIsInfoSignActive] = useState(false);

  async function saveFormData() {
    const OPERATION_STATUS = {
      SUCCESS: "SUCCESS",
      ERROR: "ERROR",
    };

    try {
      const videoInfo = await saveMetadata();
      await uploadPoster(videoInfo.posterMetadata.id);
      await uploadVideo(videoInfo.videoMetadata.id);
      displayStatusMessage(OPERATION_STATUS.SUCCESS);
    } catch (e) {
      setRecentFormErrorMessage(e.message);
      console.error(e);
      displayStatusMessage(OPERATION_STATUS.ERROR, e.message);
    }

    loadingRef.current.style.display = "none";

    /**
     * @param id {string}
     * @returns {Promise<string>}
     */
    async function uploadPoster(id) {
      const posterFile = posterInputRef.current.files[0];
      if (posterFile == null) {
        throw new Error("Необходимо выбрать постер для видео.");
      }

      const posterFormData = new FormData();
      posterFormData.append("image", posterFile);
      posterFormData.append("id", id);

      const res = await fetch(`${STORAGE_URL}/api/v0/posters/upload`, {
        method: "POST",
        body: posterFormData,
      });

      if (res.status !== 201) {
        throw new Error(
          decodeURI(res.headers.get("Message")).replaceAll("+", " "),
        );
      }
    }

    /**
     * @param id {string}
     * @returns {Promise<string>}
     */
    async function uploadVideo(id) {
      const videoFile = videoInputRef.current.files[0];
      if (videoFile == null) {
        throw new Error("Необходимо выбрать видеофайл.");
      }

      const videoFormData = new FormData();
      videoFormData.append("id", id);
      videoFormData.append("video", videoFile);

      const res = await fetch(`${STORAGE_URL}/api/v0/videos/upload`, {
        method: "POST",
        body: videoFormData,
      });

      if (res.status !== 201) {
        throw new Error(
          decodeURI(res.headers.get("Message")).replaceAll("+", " "),
        );
      }
    }

    async function saveMetadata() {
      const form = new FormData(formRef.current);
      validateFormInputs(form); // throws error if not satisfied
      const metadata = {
        contentDetails: {
          title: form.get("title").trim(),
          releaseDate: form.get("releaseDate"),
          country: form.get("country").trim(),
          mainGenre: form.get("mainGenre").trim(),
          subGenres: parseSubGenres(form.get("subGenres")),
          age: form.get("age").trim(),
          rating: form.get("rating").trim(),
        },
        posterMetadata: {
          filename: posterInputRef.current.files[0].name,
          contentType: posterInputRef.current.files[0].type,
        },
        videoMetadata: {
          filename: videoInputRef.current.files[0].name,
          contentType: videoInputRef.current.files[0].type,
        },
      };

      const res = await fetch(`${STORAGE_URL}/api/v0/metadata`, {
        method: "POST",
        headers: {
          "Content-type": "application/json",
        },
        body: JSON.stringify(metadata),
      });

      if (res.status !== 201) {
        throw new Error(
          decodeURI(res.headers.get("Message")).replaceAll("+", " "),
        );
      }

      return (await res).json();

      // TODO make additional checks for input
      /**
       * @param {string[]} subGenresString
       * @returns {string[]}
       */
      function parseSubGenres(subGenresString) {
        const splitted = subGenresString.split(",");
        if (splitted.length === 0) {
          return [];
        }
        let subGenresArray = [];
        for (const string of splitted) {
          if (string.length !== 0) subGenresArray.push(string.trim());
        }
        return subGenresArray;
      }
    }
    /**
     *
     * @param {FormData} form
     * @throws Error when input is not satisfied
     */
    function validateFormInputs(form) {
      if (!form.get("title") || isBlank(form.get("title")))
        throw new Error("Необходимо указать название");
      else if (!form.get("country") || isBlank(form.get("country")))
        throw new Error("Необходимо указать страну");
      else if (!form.get("releaseDate"))
        throw new Error("Необходимо указать дату релиза");
      else if (!form.get("mainGenre") || isBlank(form.get("mainGenre")))
        throw new Error("Необходимо указать основной жанр");
      else if (!form.get("age"))
        throw new Error("Необходимо указать возрастное ограничение");
      else if (!form.get("rating") || isBlank(form.get("rating")))
        throw new Error("Необходимо указать рейтинг");
      else if (
        isNaN(form.get("rating")) ||
        Number(form.get("rating")) < 0 ||
        Number(form.get("rating")) > 10
      ) {
        throw new Error("Рейтинг должен быть числом от 0 до 10");
      }

      // Validate Poster File
      const posterFiles = posterInputRef.current.files;
      if (!posterFiles || posterFiles.length === 0) {
        throw new Error("Необходимо загрузить постер");
      } else {
        const posterFile = posterFiles[0];
        const allowedPosterTypes = ["image/jpeg", "image/png", "image/gif"];
        if (!allowedPosterTypes.includes(posterFile.type))
          throw new Error("Неподдерживаемый тип файла для постера");
        const maxPosterSize = 5 * 1024 * 1024; // 5MB
        if (posterFile.size > maxPosterSize)
          throw new Error("Размер файла постера не должен превышать 5MB");
      }

      // Validate Video File
      const videoFiles = videoInputRef.current.files;
      if (!videoFiles || videoFiles.length === 0) {
        throw new Error("Необходимо загрузить видео");
      } else {
        const videoFile = videoFiles[0];
        // Optional: Validate file type and size
        const allowedVideoTypes = ["video/mp4", "video/avi", "video/mov"];
        if (!allowedVideoTypes.includes(videoFile.type)) {
          throw new Error("Неподдерживаемый тип файла для видео");
        }
        const maxVideoSize = 20 * 1024 * 1024 * 1024;
        if (videoFile.size > maxVideoSize) {
          throw new Error("Размер файла видео не должен превышать 20GB");
        }
      }
    }

    /**
     * Check if string is blank.
     * @param {string} str
     * @returns
     */
    function isBlank(str) {
      return !str || str.trim() === "";
    }

    /**
     * @param operationStatus {OPERATION_STATUS}
     * @param errmes {string | null}
     */
    function displayStatusMessage(operationStatus, errmes) {
      setIsInfoSignActive(false);
      const statusBar = formSaveStatus.current;
      statusBar.style.fontSize = "0.8em";
      statusBar.style.marginTop = "-6px";

      switch (operationStatus) {
        case OPERATION_STATUS.SUCCESS: {
          statusBar.textContent = "Сохранено ✅";
          statusBar.style.color = "green";
          break;
        }
        case OPERATION_STATUS.ERROR: {
          statusBar.textContent = `${errmes}`;
          statusBar.style.color = "red";
          break;
        }
        default:
          break;
      }

      setTimeout(() => {
        setIsInfoSignActive(true);
        statusBar.textContent = "";
        statusBar.style.fontSize = "0.7em";
      }, 2000);
    }
  }

  /**
   * @param {Object} props
   * @param {number} props.age
   * @returns {Element}
   */
  const AgeRadioInput = ({ age }) => (
    <div>
      <input
        onKeyDown={(event) => event.keyCode === 13 && event.preventDefault()}
        type="radio"
        name="age"
        value={age}
      />
      <label htmlFor="" className="ml-0.5">
        {age}+
      </label>
    </div>
  );

  function handleSubmitButton(event) {
    event.preventDefault();
    animateButtonPress();
    showLoadingIcon();
    saveFormData();

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

  if (!isFormAddFilmVisible) {
    return <div />;
  }

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-100 dark:bg-gray-900 p-4">
      <form
        ref={formRef}
        className="relative w-full max-w-md rounded-lg bg-white p-6 shadow-lg dark:bg-neutral-800 dark:text-blue-100"
      >
        <div className="absolute top-4 right-4">
          <button
            id="form-close-sign"
            className="cursor-pointer select-none text-xl font-bold text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300"
            onClick={closeForm}
          >
            X
          </button>
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
              className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
              type="search"
              name="country"
              // TODO restore autosuggesstion in the future
              // value={countryInput}
              // onChange={(ev) => setCountryInput(ev.target.value)}
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
              className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
              type="search"
              name="mainGenre"
              // TODO restore autosuggesstion in the future
              // value={mainGenreInput}
              // onChange={(ev) => setMainGenreInput(ev.target.value)}
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
              className="block w-full rounded-md border border-gray-300 bg-white py-2 px-3 text-sm text-gray-700 shadow-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
              type="search"
              name="subGenres"
              placeholder="разделять запятой"
              onChange={() => {}}
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
              <AgeRadioInput age={0} />
              <AgeRadioInput age={6} />
              <AgeRadioInput age={12} />
              <AgeRadioInput age={16} />
              <AgeRadioInput age={18} />
            </div>
          </li>

          <li id="posterMetadata">
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

          <li id="videoMetadata">
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
              onKeyDown={(event) =>
                event.keyCode === 13 && event.preventDefault()
              }
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
            />
            {isInfoSignActive && (
              <img
                src="assets/images/icons/info-sign.svg"
                // TODO better use popup than alert
                onClick={() => alert(recentFormErrorMessage)}
              />
            )}
            <img
              ref={loadingRef}
              src="assets/images/icons/loading.gif"
              className="hidden w-7"
            />
          </div>
          {/* Submit Button */}
          <button
            onKeyDown={(event) =>
              event.keyCode === 13 && event.preventDefault()
            }
            className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white transition-transform hover:bg-blue-700 focus:outline-none dark:bg-green-600 dark:hover:bg-green-700"
            id="add-film-button"
            onClick={handleSubmitButton}
          >
            Загрузить
          </button>
          {/* Clear Form */}
          <div className="absolute right-0">
            <button className="m-0 p-0 text-xs text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300">
              <u>Очистить форму</u>
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
