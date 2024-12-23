import { useRef } from "react";

/**
 * Film posters compilation with related metadata.
 * @param {Object} props
 * @param {ContentMetadata[]} props.metadataList - list of content metadata for each poster fetched from server
 * @returns {JSX.Element}
 */
export default function DefaultCompilation({ metadataList }) {
  const scrollableDivRef = useRef();

  // useEffect(() => {
  //     if (!isPlayerOpened) {
  //         leftArrowRef.current.style.visibility = 'hidden'
  //     }
  // }, [])

  return (
    <div className="flex flex-col justify-center">
      <div
        ref={scrollableDivRef}
        className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
      >
        {metadataList.length === 0 ? (
          <p>Постеры загружаются</p>
        ) : (
          <div className="flex w-fit gap-4 overflow-scroll bg-fuchsia-500">
            {metadataList.map((metadata, idx) => (
              <Poster key={idx} metadata={metadata} />
            ))}
          </div>
        )}
      </div>
      {/* TODO use scrollable div for mobile compatible devices */}
      {/* <SideArrows scrollableDivRef={scrollableDivRef} /> */}
    </div>
  );
}
