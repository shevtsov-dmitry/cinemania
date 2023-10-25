import {useState} from "react";

function FileInfo() {

    const [formData, setFormData] = useState({
        filmName: '',
        country: '',
        releaseDate: '',
        genre: '',
        minimalAge: '',
        imageUrl: '',
        watchTime: '',
        rating: '',
    })

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault()
        const jsonData = JSON.stringify(formData)
        console.log(jsonData);
    }

    return (<>
            <div className="container">
                <div className="add-film-header">add film</div>
                <form onSubmit={handleSubmit}>
                    <ul>
                        <li>
                            <div className="p-right-aligner">
                                <p>film name</p>
                            </div>
                            <input type="text"
                                   name="filmName"
                                   value={formData.filmName}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>country</p>
                            </div>
                            <input type="text"
                                   name="country"
                                   value={formData.country}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>release date</p>
                            </div>

                            <input type="text"
                                   name="releaseDate"
                                   value={formData.releaseDate}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>genre</p>
                            </div>
                            <input type="text"
                                   name="genre"
                                   value={formData.genre}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>minimal age</p>
                            </div>
                            <input type="text"
                                   name="minimalAge"
                                   value={formData.minimalAge}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>poster</p>
                            </div>
                            <input type="text"
                                   name="imageUrl"
                                   value={formData.imageUrl}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>watch time</p>
                            </div>
                            <input type="text"
                                   name="watchTime"
                                   value={formData.watchTime}
                                   onChange={handleInputChange}/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>rating</p>
                            </div>
                            <input type="text"
                                   name="rating"
                                   value={formData.rating}
                                   onChange={handleInputChange}/>
                        </li>
                    </ul>
                    <div className="button-aligner">
                        <button id="add-film-button">submit</button>
                    </div>
                </form>
            </div>
        </>)
}

export default FileInfo