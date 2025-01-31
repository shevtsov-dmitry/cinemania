import Constants from "@/src/constants/Constants";
import { useEffect, useState } from "react";

const FormRelatedShowsChooser = () => {
  const [showsMetadata, setShowsMetadata] = useState([]);
  const [defaultCountries, setDefaultCountries] = useState<string[]>([]);
  const [defaultGenres, setDefaultGenres] = useState<string[]>([]);
  const [errorNotificationMessage, setErrorNotificationMessage] = useState<
    string | null
  >(null);

  useEffect(() => {
    fetch(Constants.STORAGE_URL + "/api/v0/filling-assistants/countries/all")
      .then((response) => response.json())
      .then((countries) => setDefaultCountries(countries))
      .catch((error) => {
        console.error(error);
        setErrorNotificationMessage("Не удалось получить страны.");
      });

    fetch(Constants.STORAGE_URL + "/api/v0/filling-assistants/genres/all")
      .then((response) => response.json())
      .then((genres) => setDefaultGenres(genres))
      .catch((error) => {
        console.error(error);
        setErrorNotificationMessage("Не удалось получить жанры.");
      });
  }, []);

  async function findPerson(e: Event) {
    e.preventDefault();
    fetch
  }

  return (
    <form>
      <div className="flex gap-2">
        <div id="country-picker">
          <label htmlFor="country-picker">Страна</label>
          <select id="country-picker" onChange={(e) => e.target.value}>
            {defaultCountries.map((country, index) => (
              <option key={index} value={country}>
                {country}
              </option>
            ))}
          </select>
        </div>
        <div id="genre-picker">
          <label htmlFor="genre-picker">Жанр</label>
          <select id="genre-picker" onChange={(e) => e.target.value}>
            {defaultGenres.map((genre, index) => (
              <option key={index} value={genre}>
                {genre}
              </option>
            ))}
          </select>
        </div>
      </div>
      <button className="bg-blue-500 text-white p-2 font-bold">Искать</button>
      <div className="grid row-cols-3 gap-1"></div>

      <button className="bg-blue-500 text-white p-2 font-bold">
        Подтвердить
      </button>
    </form>
  );
};

export default FormRelatedShowsChooser;
