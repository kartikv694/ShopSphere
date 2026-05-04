import { createContext, useState } from "react";

export const SearchContext = createContext();

export const SearchProvider = ({ children }) => {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");

  return (
    <SearchContext.Provider
      value={{ search, setSearch, category, setCategory }}
    >
      {children}
    </SearchContext.Provider>
  );
};