import {
  Routes,
  Route,
  Link,
  Navigate,
  useNavigate,
  useLocation,
} from "react-router-dom";

import { fetchData, postData, sendToast } from "./utils/request_utils.js";
import { useAuth, UserContext } from "./context/AuthContext.js";
import "./styles/home.css";
import logo_big from "./images/logo_big.png";
import background_image from "./images/home.png";
// ------------------------------------------------------------- //
import React, { useState, useEffect } from "react";
// ------------------------------------------------------------- //
import AboutMizdooni from "./components/about.js";
import SearchBarForm from "./components/rest_search_bar.js";
import SearchResult from "./components/top_result.js";
import Header from "./components/header.js";
import Footer from "./components/footer.js";
// ------------------------------------------------------------- //
const search_method_city = "search_by_city";
const search_method_name = "search_by_name";
const search_method_type = "search_by_type";
const user_details_endpoint = "/user/";
const restaurant_search_endpoint = "/restaurants/search";
const all_restaurants = "/restaurants";
// ------------------------------------------------------------- //

function combineMaps(map1, map2) {
  let combinedMap = new Map([...map1]);

  map2.forEach((value, key) => {
    if (combinedMap.has(key)) {
      combinedMap.set(key, combinedMap.get(key) + value);
    } else {
      combinedMap.set(key, value);
    }
  });

  return combinedMap;
}

export default function Home() {
  // ------------------------------------------------------------- //
  const { authDetails, setAuthDetails } = useAuth();
  const [userDetails, setUserDetails] = useState({});
  const [userSpecificSearchResult, setUserSpecificSearchResult] = useState({});
  const [topRestaurants, setTopRestaurants] = useState({});
  const [restTypes, setRestTypes] = useState([]);
  const [restLocations, setRestLocations] = useState([]);

  // ------------------------------------------------------------- //
  const fetchRestMetaInfo = () => {
    fetchData(
      all_restaurants,
      {},
      (response) => {
        if (response.success) {
          const restTypes = [
            ...new Set(response.data.restaurants.map((val) => val.type)),
          ];
          const restLocations = [
            ...new Set(
              response.data.restaurants
                .map((val) => [val.address.city, val.address.country])
                .flat()
            ),
          ];
          setRestTypes(restTypes);
          setRestLocations(restLocations);
          const rests = response.data.restaurants;
          rests.sort((a, b) => b.avgOverallScore - a.avgOverallScore);
          setTopRestaurants({ restaurants: rests });
        }
      },
      (res) => {},
      true,
      authDetails
    );
    return;
  };

  const searchRestaurants = (search_type, key_data) => {
    postData(
      restaurant_search_endpoint,
      {
        action: search_type,
        search: key_data,
      },
      (response) => {
        setUserSpecificSearchResult(response.data);
        sendToast(
          response.success,
          response.success
            ? "Fetched your local restaurants "
            : "Could not show initial restaurants based on your location"
        );
      },
      (res) => {},
      (res) => {}
    );
  };

  const fetchUser = () => {

    if (authDetails.token) {
      fetchData(
        user_details_endpoint + authDetails.user.username,
        {},
        (response) => {
          if (response.success) {
            console.log("Fetch User Response", response);
            setUserDetails(response.data);
            const authDetailsCopy = authDetails;
            authDetailsCopy.user.address=response.data.user.address;
            searchRestaurants(
              search_method_city,
              response.data.user.address.city
            );
          }
        },
        (res) => {},
        true,
        authDetails
      );
    }
  };

  useEffect(() => {
    fetchRestMetaInfo();
    fetchUser();
  }, []);

  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <Header />
      <div className="container-s w-100 text-center">
        <div
          className="home-background w-100"
          style={{ backgroundImage: `url(${background_image})` }}
        >
          <div className="container">
            <div className="input-group mb-3">
              <img className="big-logo" src={logo_big} />
            </div>
            <SearchBarForm
              restTypes={restTypes}
              restLocations={restLocations}
            />
          </div>
        </div>
      </div>
      <SearchResult
        searchResult={topRestaurants}
        title="Top restuarants on Mizdooni"
      />
      <SearchResult
        searchResult={userSpecificSearchResult}
        title="You might also like"
      />
      <AboutMizdooni />
      <Footer />
    </main>
  );
}
