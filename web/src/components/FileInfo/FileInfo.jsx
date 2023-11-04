import {useState} from "react";

function FileInfo() {

    const [filmName, setFilmName] = useState('')
    const [country, setCountry] = useState('')
    const [releaseDate, setReleaseDate] = useState('')
    const [genre, setGenre] = useState('')
    const [minimalAge, setMinimalAge] = useState('')
    const [imageUrl, setImageUrl] = useState('')
    const [watchTime, setWatchTime] = useState('')
    const [rating, setRating] = useState('')


    function fillForm(e) {
        e.preventDefault();

        const filledForm = {
            filmName: filmName,
            country: country,
            releaseDate: releaseDate,
            genre: genre,
            minimalAge: minimalAge,
            imageUrl: imageUrl,
            watchTime: watchTime,
            rating: rating
        }

        console.log(filledForm)
    }

    const handleInputChange = (input) => {
        const {name, value} = input.target;
        setValues();
        showTypingSuggestions(input)
        console.log(input.target.positionX, input.target.positionY)


        function setValues() {
            switch (name) {
                case 'filmName':
                    setFilmName(value);
                    break;
                case 'country':
                    setCountry(value);
                    break;
                case 'releaseDate':
                    setReleaseDate(value);
                    break;
                case 'genre':
                    setGenre(value);
                    break;
                case 'minimalAge':
                    setMinimalAge(value);
                    break;
                case 'imageUrl':
                    setImageUrl(value);
                    break;
                case 'watchTime':
                    setWatchTime(value);
                    break;
                case 'rating':
                    setRating(value);
                    break;
            }
        }


        // console.log(value)
    };

    const showTypingSuggestions = () => {

        return (
            <ul className="typing-suggestions-ul">
                <li className="typing-suggestions-li">one</li>
                <li className="typing-suggestions-li">two</li>
                <li className="typing-suggestions-li">one</li>
                <li className="typing-suggestions-li">two</li>
                <li className="typing-suggestions-li">three</li>
            </ul>
        )
    }

    return (
        <div className="container">
            <div className="add-film-header">add film</div>
            <form onSubmit={fillForm}>
                <ul className="form-ul">
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>film name</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="filmName"
                               value={filmName}
                               onChange={handleInputChange}/>
                    </li>
                    {showTypingSuggestions()}
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>country</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="country"
                               value={country}
                               onChange={handleInputChange}/>
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>release date</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="releaseDate"
                               value={releaseDate}
                               onChange={handleInputChange}/>
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>genre</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="genre"
                               value={genre}
                               onChange={handleInputChange}
                        />
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>minimal age</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="minimalAge"
                               value={minimalAge}
                               onChange={handleInputChange}/>
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>poster</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="imageUrl"
                               value={imageUrl}
                               onChange={handleInputChange}/>
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>watch time</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="watchTime"
                               value={watchTime}
                               onChange={handleInputChange}/>
                    </li>
                    <li className="form-li">
                        <div className="p-right-aligner">
                            <p>rating</p>
                        </div>
                        <input className="form-input"
                               type="text"
                               name="rating"
                               value={rating}
                               onChange={handleInputChange}/>
                    </li>
                </ul>
                <div className="button-aligner">
                    <button id="add-film-button">submit</button>
                </div>
            </form>
        </div>
    )
}

export default FileInfo