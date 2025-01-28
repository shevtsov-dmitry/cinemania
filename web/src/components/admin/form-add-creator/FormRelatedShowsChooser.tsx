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

  return (
    <form>
      <div>
        <div id="country-picker">
          <label htmlFor="country-picker">Выберите связанные шоу:</label>
          <select
            id="country-picker"
            onChange={(e) => (e.target.value)}
          >
            {showsMetadata.map((show, index) => (
              <option key={index} value={show}>
                {show}
              </option>
            ))}
          </select>
        </div>
      </div>

      <button className="bg-blue-500 text-white p-2 font-bold">
        Подтвердить
      </button>
    </form>
  );
};

export default FormRelatedShowsChooser;
