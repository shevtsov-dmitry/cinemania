import {useEffect, useRef, useState} from "react";

function FileInfo() {

    // getters and setters
    const [filmName, setFilmName] = useState('')
    const [country, setCountry] = useState('')
    const [releaseDate, setReleaseDate] = useState('')
    const [genre, setGenre] = useState('')
    const [minimalAge, setMinimalAge] = useState('')
    const [imageUrl, setImageUrl] = useState('')
    const [watchTime, setWatchTime] = useState('')
    const [rating, setRating] = useState('')
    // position references
    const filmNameRef = useRef()
    const countryRef = useRef()
    const releaseDateRef = useRef()
    const genreRef = useRef()
    const minimalAgeRef = useRef()
    const imageUrlRef = useRef()
    const watchTimeRef = useRef()
    const ratingRef = useRef()
    const typeSuggestionsRef = useRef()

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

        function getElementRef(name) {
            switch (name) {
                case 'filmName':
                    return filmNameRef
                case 'country':
                    return countryRef
                case 'releaseDate':
                    return releaseDateRef
                case 'genre':
                    return genreRef
                case 'minimalAge':
                    return minimalAgeRef
                case 'imageUrl':
                    return imageUrlRef
                case 'watchTime':
                    return watchTimeRef
                case 'rating':
                    return ratingRef
            }
        }

        const elementRef = getElementRef(name)
        const position = elementRef.current.getBoundingClientRect()
        showTypingSuggestions(position)
    };

    const showTypingSuggestions = (position) => {
        const newPosition = {
            top: position.top,
            left: position.left
        }
        // typeSuggestionsRef.current.style.display = "none"
        const element = typeSuggestionsRef.current
        console.log(position.top)
        element.style.top = `${position.top + 15}px`
        element.style.left = `${position.left + 5}px`
    }

    const typingSuggestions = () => {
        return (
            <ul className="typing-suggestions-ul" ref={typeSuggestionsRef}>
                <li className="typing-suggestions-li">one</li>
                <li className="typing-suggestions-li">two</li>
                <li className="typing-suggestions-li">one</li>
                <li className="typing-suggestions-li">two</li>
                <li className="typing-suggestions-li">three</li>
            </ul>
        )
    }

    function form() {
        return <form onSubmit={fillForm}>
            <ul className="form-ul">
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>film name</p>
                    </div>
                    <input ref={filmNameRef}
                           className="form-input"
                           type="text"
                           name="filmName"
                           value={filmName}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>country</p>
                    </div>
                    <input ref={countryRef}
                           className="form-input"
                           type="text"
                           name="country"
                           value={country}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>release date</p>
                    </div>
                    <input ref={releaseDateRef}
                           className="form-input"
                           type="text"
                           name="releaseDate"
                           value={releaseDate}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>genre</p>
                    </div>
                    <input ref={genreRef}
                           className="form-input"
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
                    <input ref={minimalAgeRef}
                           className="form-input"
                           type="text"
                           name="minimalAge"
                           value={minimalAge}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>poster</p>
                    </div>
                    <input ref={imageUrlRef}
                           className="form-input"
                           type="text"
                           name="imageUrl"
                           value={imageUrl}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>watch time</p>
                    </div>
                    <input ref={watchTimeRef}
                           className="form-input"
                           type="text"
                           name="watchTime"
                           value={watchTime}
                           onChange={handleInputChange}/>
                </li>
                <li className="form-li">
                    <div className="p-right-aligner">
                        <p>rating</p>
                    </div>
                    <input ref={ratingRef}
                           className="form-input"
                           type="text"
                           name="rating"
                           value={rating}
                           onChange={handleInputChange}/>
                </li>
            </ul>
            <div className="button-aligner">
                <button id="add-film-button">submit</button>
            </div>
        </form>;
    }

    return (
        <div className="container">
            <div className="add-film-header">add film</div>
            {form()}
            {typingSuggestions()}
        </div>
    )
}

export default FileInfo