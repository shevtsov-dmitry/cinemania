import React, { useEffect, useRef, useState } from "react";
import Constants from "@/src/constants/Constants";
import useFormAddFilmStore from "@/src/state/formAddFilmState";
import "../formParts.css";

export default function FormAddFilm() {
  const STORAGE_URL = Constants.STORAGE_URL;
  
  const hideFormAddFilm = useFormAddFilmStore((state) => state.hideFormAddFilm);

  const [countryInput, setCountryInput] = useState("");
  const [mainGenreInput, setMainGenreInput] = useState("");
  const [subGenresInput, setSubGenresInput] = useState("");


  const posterInputRef = useRef();
  const trailerInputRef = useRef();
  const videoInputRef = useRef();

  const formRef = useRef();
  const formSaveStatus = useRef();
  const loadingRef = useRef();

  const [recentFormErrorMessage, setRecentFormErrorMessage] = useState("");
  const [isInfoSignActive, setIsInfoSignActive] = useState(false);

  const [isPosterFileSelected, setIsPosterFileSelected] = useState(false);
  const [isVideoShowFileSelected, setIsVideoShowFileSelected] = useState(false);
  const [isTrailerFileSelected, setIsTrailerFileSelected] = useState(false);
  const [isFilmingGroupSelected, setIsFilmingGroupSelected] = useState(false);

  async function saveFormData() {
    const OPERATION_STATUS = {
      SUCCESS: "SUCCESS",
      ERROR: "ERROR",
    };

    try {
      const videoInfo = await saveMetadata();
      await uploadPoster(videoInfo.poster.id);
      await uploadVideo(videoInfo.video.id);
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
          decodeURI(res.headers.get("Message")).replaceAll("+", " ")
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
          decodeURI(res.headers.get("Message")).replaceAll("+", " ")
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
        poster: {
          filename: posterInputRef.current.files[0].name,
          contentType: posterInputRef.current.files[0].type,
        },
        video: {
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
          decodeURI(res.headers.get("Message")).replaceAll("+", " ")
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

  return (
    <div className="container">
      <form ref={formRef} className="form">
        <div className="close-button-container">
          <button
            id="form-close-sign"
            className="close-button"
            onClick={hideFormAddFilm}
          >
            X
          </button>
        </div>

        <h2 className="form-title">Добавить фильм</h2>

        <ul className="form-list">
          <li id="title">
            <label className="label">Название фильма</label>
            <input className="input" type="search" name="title" />
          </li>

          <li className="form-row">
            <div id="country">
              <label className="label">Страна</label>
              <input className="input" type="search" name="country" />
              <div className="suggestions">{countrySuggestionsDOM}</div>
            </div>

            <div id="releaseDate">
              <label className="label">Дата релиза</label>
              <input className="input" type="date" name="releaseDate" />
            </div>
          </li>

          <li className="form-row">
            <div id="mainGenre">
              <label className="label">Основной жанр</label>
              <input className="input" type="search" name="mainGenre" />
              <div className="suggestions">{mainGenreSuggestionsDOM}</div>
            </div>

            <div id="subGenres" className="full-width">
              <label className="label">Поджанры</label>
              <input
                className="input"
                type="search"
                name="subGenres"
                placeholder="разделять запятой"
              />
              <div className="suggestions">{subGenresSuggestionsDOM}</div>
            </div>
          </li>

          <li id="description">
            <label className="label">Описание</label>
            <textarea
              name="description"
              className="textarea"
              rows={4}
              placeholder="Описание фильма"
            ></textarea>
          </li>
        </ul>

        <div className="form-actions">
          <button
            className="submit-button"
            id="add-film-button"
            onClick={handleSubmitButton}
          >
            Загрузить
          </button>
          <button className="clear-button">Очистить форму</button>
        </div>
      </form>
    </div>
  );
}
