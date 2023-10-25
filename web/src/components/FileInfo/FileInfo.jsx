function FileInfo() {
    return (<>
            <div className="container">
                <div className="add-film-header">add film</div>
                <form action="">
                    <ul>
                        <li>
                            <div className="p-right-aligner">
                                <p>film name</p>
                            </div>
                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>country</p>
                            </div>
                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>release date</p>
                            </div>

                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>genre</p>
                            </div>

                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>minimal age</p>
                            </div>
                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>poster</p>
                            </div>
                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>watch time</p>
                            </div>
                            <input type="text"/>
                        </li>
                        <li>
                            <div className="p-right-aligner">
                                <p>rating</p>
                            </div>
                            <input type="text"/>
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