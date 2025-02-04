// TODO - restore auto suggestions for country, main genre and sub genres

  // const options = {
  //   MAX_AUTO_SUGGESTIONS_DISPLAYED: 5,
  // };
  //
  // const [suggestionsDOM, setSuggestionsDOM] = useState([]);
  // const [countrySuggestionsDOM, setCountrySuggestionsDOM] = useState(<div />);
  // const [mainGenreSuggestionsDOM, setMainGenreSuggestionsDOM] = useState(
  //   <div />
  // );
  // const [subGenresSuggestionsDOM, setSubGenresSuggestionsDOM] = useState(
  //   <div />
  // );
  // const [autoSuggestionsMap, setAutoSuggestionsMap] = useState({});
  //
  // // TODO restore autosuggestions functionallity and refactor it into different class. It is better to inject it here with composition.
  // useEffect(() => {
  //   console.log(STORAGE_URL);
  //
  //   // fetchAutosuggestions();
  //
  //   async function fetchAutosuggestions() {
  //     let map = {
  //       buffer: [],
  //       recentInputLength: 0,
  //     };
  //     let response = await fetch(
  //       `${STORAGE_URL}/filling-assistants/genres/get/all`
  //     ).catch(() => {
  //       console.error(
  //         `problem fetching: ${STORAGE_URL}/filling-assistants/genres/get/all`
  //       );
  //       return;
  //     });
  //
  //     let responseData = await response.json();
  //     map = {
  //       ...map,
  //       genre: responseData,
  //     };
  //
  //     response = await fetch(
  //       `${STORAGE_URL}/filling-assistants/countries/get/all`
  //     ).catch(() => {
  //       console.error(
  //         `problem fetching: ${STORAGE_URL}/filling-assistants/countries/get/all`
  //       );
  //     });
  //     responseData = await response.json();
  //     map = {
  //       ...map,
  //       country: responseData,
  //     };
  //
  //     setAutoSuggestionsMap(map);
  //   }
  // });
  //
  // /**
  //  *
  //  * @param suggestions {string[]}
  //  * @param formFieldName {string}
  //  * @param inputValue {string}
  //  * @returns {Element} <button>
  //  */
  // function createDivFromRetrievedSuggestion(
  //   suggestions,
  //   formFieldName,
  //   inputValue
  // ) {
  //   return suggestions.map((suggestion) => createDOM(suggestion));
  //
  //   function createDOM(suggestion) {
  //     return (
  //       <button
  //         className={`bg-white px-2 text-left first:rounded-t last:rounded-b dark:bg-slate-900 dark:text-white dark:focus:bg-slate-700 dark:focus:text-teal-300`}
  //         type="submit"
  //         onClick={(ev) => {
  //           ev.preventDefault();
  //           const autoSuggestion = ev.currentTarget.textContent;
  //           changeInputTextToAutoSuggestion(autoSuggestion);
  //           setAutoSuggestionsMap({
  //             ...autoSuggestionsMap,
  //             buffer: [],
  //             recentInputLength: 0,
  //           });
  //         }}
  //         key={suggestion}
  //       >
  //         {highlightSuggestionMatchedLetters(suggestion)}
  //       </button>
  //     );
  //   }
  //
  //   function highlightSuggestionMatchedLetters(suggestion) {
  //     return (
  //       <>
  //         <span className="font-bold">
  //           {suggestion.substring(0, inputValue.length)}
  //         </span>
  //         {suggestion.substring(inputValue.length, suggestion.length)}
  //       </>
  //     );
  //   }
  //
  //   function changeInputTextToAutoSuggestion(autoSuggestion) {
  //     if (formFieldName === "country") setCountryInput(autoSuggestion);
  //     if (formFieldName === "genre") setMainGenreInput(autoSuggestion);
  //   }
  // }
  //
  // function getSuggestionsBySequence(input, list) {
  //   list = list.filter((string) => string.substring(0, input.length) === input);
  //   return list.slice(0, options.MAX_AUTO_SUGGESTIONS_DISPLAYED);
  // }
  //
  // // Set country suggestions
  // useEffect(() => {
  //   if (countryInput === undefined || countryInput === "") {
  //     return;
  //   }
  //
  //   const countries = autoSuggestionsMap.country;
  //   const buffer = autoSuggestionsMap.buffer;
  //   const recentInputLength = autoSuggestionsMap.recentInputLength;
  //   const firstCharUpCaseInput =
  //     countryInput[0].toUpperCase() +
  //     countryInput.substring(1, countryInput.length);
  //
  //   let list = countryInput.length === 1 ? countries : buffer;
  //
  //   if (countryInput.length < recentInputLength) {
  //     list = countries;
  //   }
  //
  //   list = getSuggestionsBySequence(firstCharUpCaseInput, list);
  //
  //   setAutoSuggestionsMap({
  //     ...autoSuggestionsMap,
  //     buffer: list,
  //     recentInputLength: countryInput.length,
  //   });
  //
  //   const DOM = createDivFromRetrievedSuggestion(list, "country", countryInput);
  //   setCountrySuggestionsDOM(DOM);
  // }, [countryInput]);
  //
  // // Set genre suggestions
  // useEffect(() => {
  //   if (mainGenreInput === undefined || mainGenreInput === "") {
  //     return;
  //   }
  //
  //   const genres = autoSuggestionsMap.genre;
  //   const buffer = autoSuggestionsMap.buffer;
  //   const recentInputLength = autoSuggestionsMap.recentInputLength;
  //
  //   let list = mainGenreInput.length === 1 ? genres : buffer;
  //
  //   if (mainGenreInput.length < recentInputLength) {
  //     list = genres;
  //   }
  //
  //   list = getSuggestionsBySequence(mainGenreInput, list);
  //
  //   setAutoSuggestionsMap({
  //     ...autoSuggestionsMap,
  //     buffer: list,
  //     recentInputLength: countryInput.length,
  //   });
  //
  //   const DOM = createDivFromRetrievedSuggestion(list, "genre", mainGenreInput);
  //   setMainGenreSuggestionsDOM(DOM);
  // }, [mainGenreInput]);