import Constants from "@/src/constants/Constants";
import useFilmCrewChooserStore from "@/src/state/formFilmCrewChooserState";
import ContentCreator from "@/src/types/ContentCreator";
import { FormEvent, useEffect, useState } from "react";

const CreatorItem = ({
  creator,
  onSelect,
}: {
  creator: ContentCreator;
  onSelect: (id: string) => void; 
}) => {
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  useEffect(() => {
    const fetchImage = async () => {
      try {
        const response = await fetch(
          Constants.STORAGE_URL +
            `/api/v0/metadata/content-creators/user-pics/${creator.personCategory}/${creator.userPic.id}`
        );
        if (!response.ok) {
          throw new Error(`Failed to fetch image: ${response.status}`);
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        setImageUrl(url);
      } catch (error) {
        console.error("Error fetching image:", error);
      }
    };

    fetchImage();

    return () => {
      if (imageUrl) {
        URL.revokeObjectURL(imageUrl);
      }
    };
  }, [creator]);

  return (
    <div className="flex items-center p-4 bg-gray-100 rounded border border-gray-300">
      {imageUrl ? (
        <img
          src={imageUrl}
          alt={`${creator.name} ${creator.surname}`}
          className="w-24 h-24 object-cover mr-5 rounded"
        />
      ) : (
        <div className="w-24 h-24 bg-gray-300 mr-5 rounded" />
      )}
      <div className="flex-1">
        <h3 className="text-xl font-bold text-blue-500 mb-2">{`${creator.name} ${creator.surname}`}</h3>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Category:</strong>{" "}
          {creator.personCategory}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Age:</strong>{" "}
          {creator.age}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Height:</strong>{" "}
          {creator.heightCm} cm
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Born:</strong>{" "}
          {creator.bornPlace}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Latin Name:</strong>{" "}
          {`${creator.nameLatin} ${creator.surnameLatin}`}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Status:</strong>{" "}
          {creator.isDead ? "Deceased" : "Alive"}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Birth Date:</strong>{" "}
          {creator.birthDate}
        </p>
        {creator.isDead && (
          <p className="text-base text-gray-800">
            <strong className="font-semibold text-gray-600">Death Date:</strong>{" "}
            {creator.deathDate}
          </p>
        )}
        {/* Add the Select button */}
        <button
          onClick={() => onSelect(creator.id)}
          className="mt-2 p-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-700"
        >
          Select
        </button>
      </div>
    </div>
  );
};

const FormFilmCrewChooser = () => {
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [creators, setCreators] = useState<ContentCreator[]>([]);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const hideFilmCrewChooser = useFilmCrewChooserStore((state) => state.hide);
  const setSelectedCreatorId = useFilmCrewChooserStore((state) => state.setSelectedCreatorId);

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    if (!searchTerm.trim()) {
      setErrorMessage("Please enter a name or surname to search.");
      return;
    }

    try {
      const response = await fetch(
        Constants.STORAGE_URL +
          `/api/v0/metadata/content-creators/fullname/${encodeURIComponent(
            searchTerm
          )}`
      );

      if (response.status === 400) {
        setErrorMessage("Invalid search term.");
        return;
      }
      if (response.status === 404) {
        setErrorMessage("No creators found with that name or surname.");
        setCreators([]);
        return;
      }
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data: ContentCreator[] = await response.json();
      setCreators(data);
    } catch (error) {
      console.error("Error fetching creators:", error);
      setErrorMessage("An error occurred while fetching creators.");
    }
  };

  return (
    <div className="related p-5 bg-white text-gray-800 max-w-2xl mx-auto shadow rounded">
      <div
        id="close-sign"
        className="absolute top-1 right-2 hover:cursor-pointer"
        onClick={hideFilmCrewChooser}
      >
        ✖
      </div>
      <div className="flex gap-2 mb-5 mt-3">
        <label htmlFor="search-input" className="sr-only">
          Поиск по имени\фамилии
        </label>
        <input
          id="search-input"
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by name or surname"
          className="p-2 w-72 border-2 border-blue-500 rounded text-base"
        />
        <button
          onClick={handleSearch}
          className="p-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-700"
        >
          Искать
        </button>
      </div>
      {errorMessage && <div className="text-red-500 mb-5">{errorMessage}</div>}
      <div className="flex flex-col gap-5">
        {creators.length > 0 ? (
          creators.map((creator) => (
            <CreatorItem
              key={creator.id}
              creator={creator}
              onSelect={setSelectedCreatorId} 
            />
          ))
        ) : (
          <p className="text-gray-600">Не найдено совпадений.</p>
        )}
      </div>
    </div>
  );
};

export default FormFilmCrewChooser;
