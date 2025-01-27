import { useStore } from "zustand";
import FormAddFilm from "./form-add-film/FormAddFilm";

const AdminPage = (): React.ReactElement => {
  const isFormAddFilmVisible = useStore((state: {isFormAddFilmVisible: boolean}) => state.isFormAddFilmVisible);
  
  return (
    <FormAddFilm />
  );
};

export default AdminPage;
